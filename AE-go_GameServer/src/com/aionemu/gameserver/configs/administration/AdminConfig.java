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
package com.aionemu.gameserver.configs.administration;

import com.aionemu.commons.configuration.Property;

/**
 * @author ATracer
 */
public class AdminConfig
{
	@Property(key = "gameserver.administration.command.add", defaultValue = "3")
	public static int			COMMAND_ADD;
	
	@Property(key = "gameserver.administration.command.ai", defaultValue = "3")
	public static int			COMMAND_AI;
	
	@Property(key = "gameserver.administration.command.addtitle", defaultValue = "3")
	public static int			COMMAND_ADDTITLE;
	
	@Property(key = "gameserver.administration.command.adddrop", defaultValue = "3")
	public static int			COMMAND_ADDDROP;
	
	@Property(key = "gameserver.administration.command.advsendfakeserverpacket", defaultValue = "3")
	public static int			COMMAND_ADVSENDFAKESERVERPACKET;
	
	@Property(key = "gameserver.administration.command.announce", defaultValue = "3")
	public static int			COMMAND_ANNOUNCE;
	
	@Property(key = "gameserver.administration.command.configure", defaultValue = "3")
	public static int			COMMAND_CONFIGURE;
	
	@Property(key = "gameserver.administration.command.deletespawn", defaultValue = "3")
	public static int			COMMAND_DELETESPAWN;
	
	@Property(key = "gameserver.administration.command.goto", defaultValue = "3")
	public static int			COMMAND_GOTO;
	
	@Property(key = "gameserver.administration.command.givemissingskills", defaultValue = "3")
	public static int			COMMAND_GIVEMISSINGSKILLS;
	
	@Property(key = "gameserver.administration.command.heal", defaultValue = "3")
	public static int			COMMAND_HEAL;
	
	@Property(key = "gameserver.administration.command.info", defaultValue = "3")
	public static int			COMMAND_INFO;

    @Property(key = "gameserver.administration.command.kick", defaultValue = "3")
	public static int			COMMAND_KICK;
	
	@Property(key = "gameserver.administration.command.kill", defaultValue = "3")
	public static int			COMMAND_KILL;
	
	@Property(key = "gameserver.administration.command.moveplayertoplayer", defaultValue = "3")
	public static int			COMMAND_MOVEPLAYERTOPLAYER;
	
	@Property(key = "gameserver.administration.command.moveto", defaultValue = "3")
	public static int			COMMAND_MOVETO;
	
	@Property(key = "gameserver.administration.command.movetoplayer", defaultValue = "3")
	public static int			COMMAND_MOVETOPLAYER;

	@Property(key = "gameserver.administration.command.movetome", defaultValue = "3")
	public static int			COMMAND_MOVETOME;
	
	@Property(key = "gameserver.administration.command.notice", defaultValue = "3")
	public static int			COMMAND_NOTICE;

	@Property(key = "gameserver.administration.command.promote", defaultValue = "3")
	public static int			COMMAND_PROMOTE;
	
	@Property(key = "gameserver.administration.command.questcommand", defaultValue = "3")
	public static int			COMMAND_QUESTCOMMAND;
	
	@Property(key = "gameserver.administration.command.reload", defaultValue = "3")
	public static int			COMMAND_RELOAD;

	@Property(key = "gameserver.administration.command.reloadspawns", defaultValue = "3")
	public static int			COMMAND_RELOADSPAWNS;

	@Property(key = "gameserver.administration.command.revoke", defaultValue = "3")
	public static int			COMMAND_REVOKE;
	
	@Property(key = "gameserver.administration.command.savespawndata", defaultValue = "3")
	public static int			COMMAND_SAVESPAWNDATA;
	
	@Property(key = "gameserver.administration.command.sendfakeserverpacket", defaultValue = "3")
	public static int			COMMAND_SENDFAKESERVERPACKET;
	
	@Property(key = "gameserver.administration.command.sendrawpacket", defaultValue = "3")
	public static int			COMMAND_SENDRAWPACKET;
	
	@Property(key = "gameserver.administration.command.setlevel", defaultValue = "3")
	public static int			COMMAND_SETLEVEL;
	
	@Property(key = "gameserver.administration.command.setclass", defaultValue = "3")
	public static int			COMMAND_SETCLASS;
	
	@Property(key = "gameserver.administration.command.setexp", defaultValue = "3")
	public static int			COMMAND_SETEXP;
	
	@Property(key = "gameserver.administration.command.settitle", defaultValue = "3")
	public static int			COMMAND_SETTITLE;
	
	@Property(key = "gameserver.administration.command.spawnnpc", defaultValue = "3")
	public static int			COMMAND_SPAWNNPC;
	
	@Property(key = "gameserver.administration.command.unloadspawn", defaultValue = "3")
	public static int			COMMAND_UNLOADSPAWN;
	
	@Property(key = "gameserver.administration.command.addskill", defaultValue = "3")
	public static int			COMMAND_ADDSKILL;
	
	@Property(key = "gameserver.administration.command.speed", defaultValue = "3")
	public static int			COMMAND_SPEED;
	
	@Property(key = "gameserver.administration.command.system", defaultValue = "3")
	public static int			COMMAND_SYSTEM;

    @Property(key = "gameserver.administration.command.unstuck", defaultValue = "3")
    public static int           COMMAND_UNSTUCK;
    
    @Property(key = "gameserver.administration.command.weather", defaultValue = "3")
	public static int			COMMAND_WEATHER;
    
    @Property(key = "gameserver.administration.command.zone", defaultValue = "3")
    public static int           COMMAND_ZONE;
    
    @Property(key = "gameserver.administration.command.legion", defaultValue = "3")
    public static int           COMMAND_LEGION;
    
    @Property(key = "gameserver.administration.command.prison", defaultValue = "3")
    public static int			COMMAND_PRISON;
}