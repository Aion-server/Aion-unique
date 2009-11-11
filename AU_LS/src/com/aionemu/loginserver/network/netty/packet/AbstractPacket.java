package com.aionemu.loginserver.network.netty.packet;


/**
 * @author ATracer
 */
public abstract class AbstractPacket
{
	protected int opCode;
	
	/**
	 * 
	 * @param opCode
	 */
	public AbstractPacket(int opCode)
	{
		super();
		this.opCode = opCode;
	}

	/**
	 * @return the opCode
	 */
	public int getOpCode()
	{
		return opCode;
	}
}
