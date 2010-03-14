/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

import javolution.util.FastMap;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.templates.mail.MailMessage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author kosyachok
 *
 */
public class MailService
{
	private IDFactory aionObjectsIDFactory;
	private World world;
	
	@Inject
	public MailService(@IDFactoryAionObject IDFactory aionObjectsIDFactory, World world)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.world = world;
	}
	
	@Inject
	ServiceProxy sp;
	
	public void sendMail(Player sender, String recipientName, String title, String message, int attachedItemObjId, int attachedItemCount, int attachedKinahCount, boolean express)
	{
		if(express)  //TODO express mail
			return;
		
		PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(recipientName, world);
		Player onlineRecipient;
		
		if(recipientCommonData == null)
		{
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.NO_SUCH_CHARACTER_NAME));
			return;
		}
		
		
		if(recipientCommonData.getRace().getRaceId() != sender.getCommonData().getRace().getRaceId())
		{
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.MAIL_IS_ONE_RACE_ONLY));
			return;
		}
		
		
		if(recipientCommonData.isOnline())
		{
			onlineRecipient = world.findPlayer(recipientCommonData.getPlayerObjId());
			if(!onlineRecipient.getMailbox().haveFreeSlots())
			{
				PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.RECIPIENT_MAILBOX_FULL));
				return;
			}
		}
		else
		{
			if(recipientCommonData.getMailboxLetters() >= 100)
			{
				PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.RECIPIENT_MAILBOX_FULL));
				return;
			}
			onlineRecipient = null;
		}
		
		
		if(!validateMailSendPrice(sender, attachedKinahCount, attachedItemObjId, attachedItemCount))
			return;
		
		
		Item attachedItem = null;
		int finalAttachedKinahCount = 0;
		
		int kinahMailCommission = 0;
		int itemMailCommission = 0;
		
		Storage senderInventory = sender.getInventory();
		
		if(attachedItemObjId != 0)
		{
			Item senderItem = senderInventory.getItemByObjId(attachedItemObjId);
			
			if(senderItem != null)
			{
				float qualityPriceRate;
				switch(senderItem.getItemTemplate().getItemQuality())
				{
					case JUNK:
					case COMMON:
						qualityPriceRate = 0.02f;
						break;
						
					case RARE:
						qualityPriceRate = 0.03f;
						break;
						
					case LEGEND:
					case UNIQUE:
						qualityPriceRate = 0.04f;
						break;
						
					case MYTHIC:
					case EPIC:
						qualityPriceRate = 0.05f;
						break;
					
					default:
						qualityPriceRate = 0.02f;
						break;
				}
				
				if(senderItem.getItemCount() == attachedItemCount)
				{
					senderInventory.removeFromBag(senderItem, false);
					PacketSendUtility.sendPacket(sender, new SM_DELETE_ITEM(attachedItemObjId));
					
					senderItem.setEquipped(false);
					senderItem.setEquipmentSlot(0);
					senderItem.setItemLocation(StorageType.MAILBOX.getId());
					
					attachedItem = senderItem;
					
					itemMailCommission = Math.round((senderItem.getItemTemplate().getPrice() * attachedItem.getItemCount()) * qualityPriceRate);
					
					DAOManager.getDAO(InventoryDAO.class).store(attachedItem, recipientCommonData.getPlayerObjId());
				}
				else if(senderItem.getItemCount() > attachedItemCount)
				{
					attachedItem = sp.getItemService().newItem(senderItem.getItemTemplate().getTemplateId(), attachedItemCount);
					senderItem.decreaseItemCount(attachedItemCount);
					PacketSendUtility.sendPacket(sender, new SM_UPDATE_ITEM(senderItem));
					
					attachedItem.setEquipped(false);
					attachedItem.setEquipmentSlot(0);
					attachedItem.setItemLocation(StorageType.MAILBOX.getId());
					
					itemMailCommission = Math.round((attachedItem.getItemTemplate().getPrice() * attachedItem.getItemCount()) * qualityPriceRate);
					
					DAOManager.getDAO(InventoryDAO.class).store(attachedItem, recipientCommonData.getPlayerObjId());
				}
			}				
		}
		
		
		if(attachedKinahCount > 0)
		{
			if(senderInventory.getKinahItem().getItemCount() - attachedKinahCount >= 0)
			{
				senderInventory.decreaseKinah(attachedKinahCount);
				finalAttachedKinahCount = attachedKinahCount;
				kinahMailCommission = Math.round(attachedKinahCount * 0.01f);
			}
		}
		
		
		Letter newLetter = new Letter(aionObjectsIDFactory.nextId(), recipientCommonData.getPlayerObjId(), attachedItem, finalAttachedKinahCount, title, message, sender.getName(), true, express);
		
		Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());

		
		if(onlineRecipient != null)
		{			 
			 Mailbox recipientMailbox = onlineRecipient.getMailbox();			 
			 recipientMailbox.putLetterToMailbox(time, newLetter);
				 
			 PacketSendUtility.sendPacket(onlineRecipient, new SM_MAIL_SERVICE(onlineRecipient, onlineRecipient.getMailbox().getLettersOnly()));
			 PacketSendUtility.sendPacket(onlineRecipient, new SM_MAIL_SERVICE(false, false));
			 PacketSendUtility.sendPacket(onlineRecipient, new SM_MAIL_SERVICE(true, true));
		}
		
		
		int finalMailCommission = 10 + kinahMailCommission + itemMailCommission;
		
		senderInventory.decreaseKinah(finalMailCommission);
		
		
		if(DAOManager.getDAO(MailDAO.class).storeLetter(time, newLetter))
		{
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.MAIL_SEND_SECCESS));
		}
	}
	
	public void readMail(Player player, int letterId)
	{
		FastMap<Timestamp, Letter> openedLetter = player.getMailbox().getLetterFromMailboxWithTimestamp(letterId);
		if(openedLetter.size() == 0)
			return;
		
		Iterator<Timestamp> iterator = openedLetter.keySet().iterator();
		
		Timestamp time = iterator.next();
		Letter oLetter = openedLetter.get(time);
		
		PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player, oLetter, time.getTime()));
		
		oLetter.setReadLetter();
	}
	
	public void getAttachments(Player player, int letterId, int attachmentType)
	{
		Letter letter = player.getMailbox().getLetterFromMailbox(letterId);
		
		if(letter == null)
			return;
		
		switch(attachmentType)
		{
			case 0:
			{
				Item attachedItem = letter.getAttachedItem();
				if(attachedItem == null)
					return;
				if(player.getInventory().isFull())
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.MSG_FULL_INVENTORY);
					return;
				}
				Item inventoryItem = player.getInventory().putToBag(attachedItem);
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(Collections.singletonList(inventoryItem)));
				PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(letterId, attachmentType));
				letter.removeAttachedItem();
				break;
			}
			case 1:
			{
				player.getInventory().increaseKinah(letter.getAttachedKinah());
				PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(letterId, attachmentType));
				letter.removeAttachedKinah();
				break;
			}				
		}
	}
	
	public void deleteMail(Player player, int letterId)
	{
		Mailbox mailbox = player.getMailbox();
		
		mailbox.deleteLetter(letterId);
		DAOManager.getDAO(MailDAO.class).deleteLetter(letterId);
		PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(letterId));
	}
	
	private boolean validateMailSendPrice(Player sender, int attachedKinahCount, int attachedItemObjId, int attachedItemCount)
	{
		int itemMailCommission = 0;
		int	kinahMailCommission = Math.round(attachedKinahCount * 0.01f);
		if(attachedItemObjId != 0)
		{
			Item senderItem = sender.getInventory().getItemByObjId(attachedItemObjId);
			float qualityPriceRate;
			switch(senderItem.getItemTemplate().getItemQuality())
			{
				case JUNK:
				case COMMON:
					qualityPriceRate = 0.02f;
					break;
					
				case RARE:
					qualityPriceRate = 0.03f;
					break;
					
				case LEGEND:
				case UNIQUE:
					qualityPriceRate = 0.04f;
					break;
					
				case MYTHIC:
				case EPIC:
					qualityPriceRate = 0.05f;
					break;
				
				default:
					qualityPriceRate = 0.02f;
					break;
			}
			
			itemMailCommission = Math.round((senderItem.getItemTemplate().getPrice() * attachedItemCount) * qualityPriceRate);
		}
		
		int finalMailPrice = 10 + itemMailCommission + kinahMailCommission;
		
		if(sender.getInventory().getKinahItem().getItemCount() >= finalMailPrice)
			return true;
		
		return false;
	}
}