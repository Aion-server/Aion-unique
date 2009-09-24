package com.aionemu.gameserver.network.aion.serverpackets;
 
 import com.aionemu.gameserver.network.aion.AionConnection;
 import com.aionemu.gameserver.network.aion.AionServerPacket;
 import java.nio.ByteBuffer;
 import java.util.Random;
 
 public class SM_ATTACK extends AionServerPacket
 {
   private int attackerobjectid;
   private int targetObjectId;
   private int attackno;
   private int time;
   private int type;
 
   public SM_ATTACK(int attackerobjectid, int targetObjectId, int attackno, int time, int type)
   {
     this.attackerobjectid = attackerobjectid;
     this.targetObjectId = targetObjectId;
     this.attackno = (attackno + 1);
     this.time = time;
     this.type = type;
   }
 
   protected void writeImpl(AionConnection con, ByteBuffer buf)
   {
     writeD(buf, this.attackerobjectid);
     writeC(buf, this.attackno);
     writeH(buf, this.time);
     writeC(buf, this.type);
 
     writeD(buf, this.targetObjectId);
     writeC(buf, this.attackno + 1);
     writeH(buf, 84);
     writeC(buf, 0);
 
     Random generator = new Random();
     int randomdamage = generator.nextInt(100) + 1;
     writeC(buf, 1);
     writeD(buf, randomdamage);
     writeH(buf, 10);
 
     writeC(buf, 0);
   }
 }