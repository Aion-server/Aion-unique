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
package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;

/**
 * @author kosyachok
 *
 */
public class MySQL5MailDAO extends MailDAO
{
	private static final Logger log = Logger.getLogger(MySQL5MailDAO.class);
	
	public Mailbox loadPlayerMailbox(Player player)
	{
		final Mailbox mailbox = new Mailbox();
		final int playerId = player.getObjectId();
		
		DB.select("SELECT * FROM mail WHERE mailRecipientId = ?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				List<Item> mailboxItems = loadMailboxItems(playerId);
				while(rset.next())
				{
					int mailUniqueId = rset.getInt("mailUniqueId");
					int recipientId = rset.getInt("mailRecipientId");
					String senderName = rset.getString("senderName");
					String mailTitle = rset.getString("mailTitle");
					String mailMessage = rset.getString("mailMessage");
					int unread = rset.getInt("unread");
					int attachedItemId = rset.getInt("attachedItemId");
					int attachedKinahCount = rset.getInt("attachedKinahCount");
					int express = rset.getInt("express");
					Timestamp recievedTime = rset.getTimestamp("recievedTime");
					Item attachedItem = null;
					if(attachedItemId != 0)
						for(Item item : mailboxItems)
							if(item.getObjectId() == attachedItemId)
							{
								if(item.getItemTemplate().isArmor() || item.getItemTemplate().isWeapon())
									DAOManager.getDAO(ItemStoneListDAO.class).load(item);
								
								attachedItem = item;
							}
							
					Letter letter = new Letter(mailUniqueId, recipientId, attachedItem, attachedKinahCount, mailTitle,
						mailMessage, senderName, recievedTime, unread == 1, express == 1);
					letter.setPersistState(PersistentState.UPDATED);
					mailbox.putLetterToMailbox(letter);
				}
			}
		});
		
		return mailbox;
	}
	
	private List<Item> loadMailboxItems(final int playerId)
	{
		final List<Item> mailboxItems = new ArrayList<Item>();
		
		DB.select("SELECT * FROM inventory WHERE `itemOwner` = ? AND `itemLocation` = 127", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					int itemUniqueId = rset.getInt("itemUniqueId");
					int itemId = rset.getInt("itemId");
					int itemCount = rset.getInt("itemCount");
					int itemColor = rset.getInt("itemColor");
					int isEquiped = rset.getInt("isEquiped");
					int slot = rset.getInt("slot");
					Item item = new Item(itemUniqueId, itemId, itemCount, itemColor, isEquiped == 1, slot, StorageType.MAILBOX.getId());
					item.setPersistentState(PersistentState.UPDATED);
					mailboxItems.add(item);
				}
			}
		});
		
		return mailboxItems;
	}
	
	public void storeMailbox(Player player)
	{
		Mailbox mailbox = player.getMailbox();
		
		Collection<Letter> letters = mailbox.getLetters();		
		for(Letter letter : letters)
		{
			storeLetter(letter.getTimeStamp(), letter);
		}		
	}
	
	public boolean storeLetter(Timestamp time, Letter letter)
	{
		boolean result = false;
		switch(letter.getLetterPersistentState())
		{
			case NEW:
				result = saveLetter(time, letter);
				break;
			
			case UPDATE_REQUIRED:
				result = updateLetter(time, letter);
				break;
			/*	
			case DELETED:
				return deleteLetter(letter);*/
		}
		letter.setPersistState(PersistentState.UPDATED);
		
		return result;
	}
	
	private boolean saveLetter(final Timestamp time, final Letter letter)
	{
		int attachedItemId = 0;
		if(letter.getAttachedItem() != null)
			attachedItemId = letter.getAttachedItem().getObjectId();
			
		final int fAttachedItemId = attachedItemId;
		
		return DB.insertUpdate("INSERT INTO `mail` (`mailUniqueId`, `mailRecipientId`, `senderName`, `mailTitle`, `mailMessage`, `unread`, `attachedItemId`, `attachedKinahCount`, `express`, `recievedTime`) VALUES(?,?,?,?,?,?,?,?,?,?)", new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, letter.getObjectId());
				stmt.setInt(2, letter.getRecipientId());
				stmt.setString(3, letter.getSenderName());
				stmt.setString(4, letter.getTitle());
				stmt.setString(5, letter.getMessage());
				stmt.setBoolean(6, letter.isUnread());
				stmt.setInt(7, fAttachedItemId);
				stmt.setInt(8, letter.getAttachedKinah());
				stmt.setBoolean(9, letter.isExpress());
				stmt.setTimestamp(10, time);
				stmt.execute();
			}
		});
	}
	
	private boolean updateLetter(final Timestamp time, final Letter letter)
	{
		int attachedItemId = 0;
		if(letter.getAttachedItem() != null)
			attachedItemId = letter.getAttachedItem().getObjectId();
		
		final int fAttachedItemId = attachedItemId;
		
		return DB.insertUpdate("UPDATE mail SET  unread=?, attachedItemId=?, attachedKinahCount=?, recievedTime=? WHERE mailUniqueId=?", new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setBoolean(1, letter.isUnread());
				stmt.setInt(2, fAttachedItemId);
				stmt.setInt(3, letter.getAttachedKinah());
				stmt.setTimestamp(4, time);
				stmt.setInt(5, letter.getObjectId());
				stmt.execute();
			}
		});
	}
	
	public boolean deleteLetter (final int letterId)
	{
		return DB.insertUpdate("DELETE FROM mail WHERE mailUniqueId=?", new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, letterId);
				stmt.execute();
			}
		});
	}
	
	@Override
	public int[] getUsedIDs() 
	{
		PreparedStatement statement = DB.prepareStatement("SELECT mailUniqueId FROM mail", ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);

		try
		{
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for(int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("mailUniqueId");
			}
			return ids;
		}
		catch(SQLException e)
		{
			log.error("Can't get list of id's from mail table", e);
		}
		finally
		{
			DB.close(statement);
		}

		return new int[0];
	}
	
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
