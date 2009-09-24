package com.aionemu.gameserver.network.aion.clientpackets;
 
 import com.aionemu.gameserver.model.gameobjects.player.Player;
 import com.aionemu.gameserver.network.aion.AionClientPacket;
 import com.aionemu.gameserver.network.aion.AionConnection;
 import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
 import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
 import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
 import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOP_STATUS;
 import com.aionemu.gameserver.utils.PacketSendUtility;
 
 public class CM_ATTACK extends AionClientPacket
 {
   private int targetObjectId;
   private int attackno;
   private int time;
   private int type;
 
   public CM_ATTACK(int opcode)
   {
    super(opcode);
   }
 
   protected void readImpl()
   {
     this.targetObjectId = readD();
     this.attackno = readC();
     this.time = readH();
     this.type = readC();
   }
 
   protected void runImpl()
   {
     Player player = ((AionConnection)getConnection()).getActivePlayer();
     int playerobjid = player.getObjectId();
     sendPacket(new SM_ATTACK(playerobjid, this.targetObjectId, this.attackno, this.time, this.type));
     if (this.attackno % 2 == 0)
     {
       sendPacket(new SM_ATTACK(this.targetObjectId, playerobjid, this.attackno + 2, this.time, this.type));
     }
 
     sendPacket(new SM_ATTACK_STATUS(this.targetObjectId, this.attackno));
     if (this.attackno % 5 != 0)
       return;
     sendPacket(new SM_EMOTION(this.targetObjectId, 13, playerobjid));
     sendPacket(new SM_LOOP_STATUS(this.targetObjectId, 0));
     PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player.getObjectId(), unknown, emotion), true);
   }
 }