package com.aionemu.loginserver.network.netty.handler;

import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.aion.AbstractClientPacket;
import com.aionemu.loginserver.network.aion.AbstractServerPacket;
import com.aionemu.loginserver.network.aion.ClientPacketHandler;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.aion.serverpackets.SM_INIT;
import com.aionemu.loginserver.network.ncrypt.CryptEngine;
import com.aionemu.loginserver.network.ncrypt.EncryptedRSAKeyPair;
import com.aionemu.loginserver.network.ncrypt.KeyGen;
import com.aionemu.loginserver.service.AccountService;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.security.interfaces.RSAPrivateKey;

/**
 * @author ATracer
 */
@ChannelPipelineCoverage("one") 
public class ClientChannelHandler extends AbstractChannelHandler
{
	private static final Logger logger = Logger.getLogger(ClientChannelHandler.class);

	private final AccountController accountController;

	private final AccountService accountService;

	private final ClientPacketHandler clientPacketHandler;

	private final KeyGen keygen;

	private CryptEngine cryptEngine;
	private EncryptedRSAKeyPair encryptedRSAKeyPair;
	private SessionKey sessionKey;
	private int sessionId = hashCode();
	private State state;
	private Account account;
	private boolean joinedGs;

	public ClientChannelHandler(AccountController accountController, AccountService accountService,
								ClientPacketHandler clientPacketHandler, KeyGen keygen) {
		this.accountController = accountController;
		this.accountService = accountService;
		this.clientPacketHandler = clientPacketHandler;
		this.keygen = keygen;
	}


	/* (non-Javadoc)
		 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
		 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception 
	{
		super.channelConnected(ctx, e);
		logger.info("Channel connected: " + sessionId);
		
		encryptedRSAKeyPair = keygen.getEncryptedRSAKeyPair();
		SecretKey blowfishKey = keygen.generateBlowfishKey();
		cryptEngine = new CryptEngine();
		cryptEngine.updateKey(blowfishKey.getEncoded());
		state = State.CONNECTED;
		inetAddress =  ((InetSocketAddress) e.getChannel().getRemoteAddress()).getAddress();
		channel = ctx.getChannel();
		
		sendPacket(new SM_INIT(this, blowfishKey));
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		/**
		 * Remove account only if not joined GameServer yet.
		 */
		if ((account != null) && !joinedGs)
		{
			accountController.removeAccountOnLS(account);
			accountService.onLogout(account);
		}
	}


	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception 
	{
		super.messageReceived(ctx, e);
		/**
		 *  Packet is frame decoded and decrypted at this stage
		 *  Here packet will be read and submitted to execution
		 */
		logger.info("Message received");
		AbstractClientPacket clientPacket = clientPacketHandler.handle((ChannelBuffer) e.getMessage(), this);
		if(clientPacket != null && clientPacket.read())
		{
			clientPacket.run();
		}
	}
	
	protected final void onServerClose()
	{
		// TODO mb some packet should be send to client before closing?
		close();
	}
	
	/**
	 * 
	 * @param packet
	 */
	public void sendPacket(AbstractServerPacket packet)
	{
		ChannelBuffer cb = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, 2*8192);
		packet.write(this, cb);
		channel.write(cb);
	}
	
	public void setJoinedGs()
	{
		joinedGs = true;
	}

	/**
	 * Possible states of AionConnection
	 */
	public static enum State
	{
		/**
		 * Means that client just connects
		 */
		CONNECTED,

		/**
		 * Means that clients GameGuard is authenticated
		 */
		AUTHED_GG,

		/**
		 * Means that client is logged in.
		 */
		AUTHED_LOGIN
	}

	/**
	 * @return the state
	 */
	public State getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state)
	{
		this.state = state;
	}

	/**
	 * Return Scrambled modulus
	 * 
	 * @return Scrambled modulus
	 */
	public final byte[] getEncryptedModulus()
	{
		return encryptedRSAKeyPair.getEncryptedModulus();
	}

	/**
	 * Return RSA private key
	 * 
	 * @return rsa private key
	 */
	public final RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) encryptedRSAKeyPair.getRSAKeyPair().getPrivate();
	}

	/**
	 * Returns unique sessionId of this connection.
	 * 
	 * @return SessionId
	 */
	public final int getSessionId()
	{
		return sessionId;
	}

	/**
	 * @return the cryptEngine
	 */
	public CryptEngine getCryptEngine()
	{
		return cryptEngine;
	}

	/**
	 * @return the sessionKey
	 */
	public SessionKey getSessionKey()
	{
		return sessionKey;
	}

	/**
	 * @param sessionKey the sessionKey to set
	 */
	public void setSessionKey(SessionKey sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	/**
	 * @return the account
	 */
	public Account getAccount()
	{
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account)
	{
		this.account = account;
	}
}
