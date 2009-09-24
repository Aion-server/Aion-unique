package com.aionemu.gameserver.network.aion.serverpackets;
 
 import com.aionemu.gameserver.network.aion.AionConnection;
 import com.aionemu.gameserver.network.aion.AionServerPacket;
 import java.nio.ByteBuffer;
 
 public class SM_ATTACK_STATUS extends AionServerPacket
 {
   private int targetObjectId;
   private int attackno;
   private int remainHp;
 
   public SM_ATTACK_STATUS(int targetObjectId, int attackno)
   {
     this.targetObjectId = targetObjectId;
     this.attackno = (attackno + 1);
   }
 
   protected void writeImpl(AionConnection con, ByteBuffer buf)
   {
     this.remainHp = (100 - (this.attackno % 5 * 20));
     if (this.remainHp == 100)
     {
       this.remainHp = 0;
     }
 
     writeD(buf, this.targetObjectId);
     writeD(buf, 0);
     writeC(buf, 5);
     writeH(buf, this.remainHp);
     writeC(buf, 0);
     writeC(buf, 148);
   }
 }