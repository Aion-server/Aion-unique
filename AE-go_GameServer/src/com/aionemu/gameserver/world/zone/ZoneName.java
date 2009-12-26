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
package com.aionemu.gameserver.world.zone;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 *
 */
@XmlType(name = "ZoneName")
@XmlEnum
public enum ZoneName
{
	//Poeta
	DEFORESTED_AREA,
	WORG_RUN,
	DAMINU_FOREST,
	MELPONEHS_CAMPSITE,
	AGERS_FARM,
	AKARIOS_VILLAGE,
	TIMOLIA_MINE,
	KABARAH_STRIP_MINE,
	FEIRAS_DOCK,
	AKARIOS_PLAINS,
	KALESS_FARM,
	CLIONA_LAKE,
	NYMPHS_POND,
	AGARIC_SPORE_ROAD,
	//Ishalgen
	DUBARO_VINE_CANYON,
	ANTUROON_SENTRY_POST,
	SAP_FARM,
	ISHALGEN_PRISON_CAMP,
	ODELLA_PLANTATION,
	ALDELLE_HILL,
	MUNIHELE_FOREST,
	NEGIS_DOCK,
	THE_FORSAKEN_HOLLOW,
	ANTUROON_COAST,
	ISHALGEN_SENTRY_POST,
	LAKE_TUNAPRE,
	ALDELLE_VILLAGE,
	EYVINDR_ANCHORAGE,
	KARDS_CAMPSITE,
	ALDELLE_BASIN,
	GUHEITUNS_TENT,
	ANTUROON_CROSSING,
	DARU_SPRING,
	HATATAS_HIDEOUT;
}
