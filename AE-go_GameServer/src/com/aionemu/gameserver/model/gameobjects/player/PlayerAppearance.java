/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.model.gameobjects.player;

/**
 * 
 * This class was very very very very very boring to write. Damn NC should die for making me spend 5 hours on writing
 * stupid saving/loading code for players. DIE, DIE, DIE!!!
 * 
 * @author SoulKeeper
 */
public class PlayerAppearance
{

	/**
	 * Player's face
	 */
	private int	face;

	/**
	 * Player's hair
	 */
	private int	hair;

	/**
	 * Dunno what it is, we should ask Nemesiss
	 */
	private int	deco;

	/**
	 * Some pretty tattoo
	 */
	private int	tattoo;

	/**
	 * Skin color in rgb? Maybe we will manage to create some sexy lesbians ;)
	 */
	private int	skinRGB;

	/**
	 * With pink hair
	 */
	private int	hairRGB;

	/**
	 * Juicy lips
	 */
	private int	lipRGB;

	/**
	 * Oval face
	 */
	private int	eyeColor;
	
	/**
	 * Eye color
	 */
	private int	faceShape;

	/**
	 * Nice forehead
	 */
	private int	forehead;

	/**
	 * Very very high eyes
	 */
	private int	eyeHeight;

	/**
	 * With big big eyes
	 */
	private int	eyeSpace;

	/**
	 * That are also wide
	 */
	private int	eyeWidth;

	/**
	 * Well, i said that they must be big?
	 */
	private int	eyeSize;

	/**
	 * And ronnd!!!!
	 */
	private int	eyeShape;

	/**
	 * With almost not noticable angle
	 */
	private int	eyeAngle;

	/**
	 * And hight brows
	 */
	private int	browHeight;

	/**
	 * That are paralel to the eyes
	 */
	private int	browAngle;

	/**
	 * and wavy
	 */
	private int	browShape;

	/**
	 * with small nose
	 */
	private int	nose;

	/**
	 * And almost unseen nose bridge
	 */
	private int	noseBridge;

	/**
	 * With verry narrow nose
	 */
	private int	noseWidth;

	/**
	 * and a bit tippy nose
	 */
	private int	noseTip;

	/**
	 * With flat cheks
	 */
	private int	cheek;

	/**
	 * Almost without lips
	 */
	private int	lipHeight;

	/**
	 * And biiiiig mout
	 */
	private int	mouthSize;

	/**
	 * And very small lips
	 */
	private int	lipSize;

	/**
	 * With pretty smile
	 */
	private int	smile;

	/**
	 * I said that hentai girls doesn't have lips!!!
	 */
	private int	lipShape;

	/**
	 * And also they don't have jaws :)
	 */
	private int	jawHeigh;

	/**
	 * Chin is not jutty
	 */
	private int	chinJut;

	/**
	 * And ears are hidden under the heair
	 */
	private int	earShape;

	/**
	 * Head is obiously big
	 */
	private int	headSize;

	/**
	 * Sexy thick neck
	 */
	private int	neck;

	/**
	 * Not to big, middle size
	 */
	private int	neckLength;

	/**
	 * Narrow shoulders
	 */
	private int	shoulders;

	/**
	 * Also not big torso
	 */
	private int	torso;

	/**
	 * But very very very sexy tits!!!
	 */
	private int	chest;

	/**
	 * Sure that our hentai girl is perfect, she has 60 sm waist
	 */
	private int	waist;

	/**
	 * and 90 sm hips :)
	 */
	private int	hips;

	/**
	 * Not very thick arms
	 */
	private int	armThickness;

	/**
	 * And hands are cute, with with manicure
	 */
	private int	handSize;

	/**
	 * Sure that legs are without any cellulite
	 */
	private int	legThicnkess;

	/**
	 * European 36-38 size
	 */
	private int	footSize;

	/**
	 * With perfect dunno-what-it-is
	 */
	private int	facialRate;

	/**
	 * And extremely seductive voice
	 */
	private int	voice;
	/**
	 * And height
	 */
	private float	height;

	// Thanks for reading my weird commens :D
	// But this is so booooooooring to write javadoc to data holder
	// ZZZZzzzzzZZZZZzzzzZZZZzzzz.....

	/**
	 * Returns character face
	 * 
	 * @return character face
	 */
	public int getFace()
	{
		return face;
	}

	/**
	 * Sets character's face
	 * 
	 * @param face
	 *            characters face
	 */
	public void setFace(int face)
	{
		this.face = face;
	}

	/**
	 * Returns character's hair
	 * 
	 * @return characters hair
	 */
	public int getHair()
	{
		return hair;
	}

	/**
	 * Sets charaxcters hair
	 * 
	 * @param hair
	 *            characters hair
	 */
	public void setHair(int hair)
	{
		this.hair = hair;
	}

	/**
	 * Returns dunno what is this
	 * 
	 * @return some crap, ask Neme what it is
	 */
	public int getDeco()
	{
		return deco;
	}

	/**
	 * Sets some crap, ask Neme what it is
	 * 
	 * @param deco
	 *            crap
	 */
	public void setDeco(int deco)
	{
		this.deco = deco;
	}

	/**
	 * Returns sexy tattoo
	 * 
	 * @return sexy tattoo
	 */
	public int getTattoo()
	{
		return tattoo;
	}

	/**
	 * Set's sexy tattoo.<br>
	 * Not sexy will throw NotSexyTattooException. Just kidding ;)
	 * 
	 * @param tattoo
	 *            some tattoo
	 */
	public void setTattoo(int tattoo)
	{
		this.tattoo = tattoo;
	}

	/**
	 * Skin color, let's create pink lesbians :D
	 * 
	 * @return skin color
	 */
	public int getSkinRGB()
	{
		return skinRGB;
	}

	/**
	 * Here is the valid place to make lesbians skin pink
	 * 
	 * @param skinRGB
	 *            skin color
	 */
	public void setSkinRGB(int skinRGB)
	{
		this.skinRGB = skinRGB;
	}

	/**
	 * Hair color, personally i prefer brunettes
	 * 
	 * @return har color
	 */
	public int getHairRGB()
	{
		return hairRGB;
	}

	/**
	 * Sets hair colors. Blonds must pass IQ test ;)
	 * 
	 * @param hairRGB
	 *            Hair color
	 */
	public void setHairRGB(int hairRGB)
	{
		this.hairRGB = hairRGB;
	}

	/**
	 * Lips color.
	 * 
	 * @return lips color
	 */
	public int getLipRGB()
	{
		return lipRGB;
	}

	/**
	 * Sets lips color
	 * 
	 * @param lipRGB
	 *            face shape
	 */
	public void setLipRGB(int lipRGB)
	{
		this.lipRGB = lipRGB;
	}


	public void setEyeRGB(int eyeColor)
	{
		this.eyeColor = eyeColor;
	}
	
	/**
	 * Gets eye color, / | \.
	 * 
	 * @return eyeColor
	 * 
	 */
	
	public int getEyeRGB()
	{
		return eyeColor;
	}
	
	/**
	 * Returns face shape
	 * 
	 * @return face shape
	 */
	public int getFaceShape()
	{
		return faceShape;
	}

	/**
	 * Sets face shape
	 * 
	 * @param faceShape
	 *            face shape
	 */
	public void setFaceShape(int faceShape)
	{
		this.faceShape = faceShape;
	}

	/**
	 * Returns forehead
	 * 
	 * @return forehead
	 */
	public int getForehead()
	{
		return forehead;
	}

	/**
	 * Sets forehead
	 * 
	 * @param forehead
	 *            size
	 */
	public void setForehead(int forehead)
	{
		this.forehead = forehead;
	}

	/**
	 * Returns eye heigth
	 * 
	 * @return eye height
	 */
	public int getEyeHeight()
	{
		return eyeHeight;
	}

	/**
	 * Sets eye heigth
	 * 
	 * @param eyeHeight
	 *            eye heigth
	 */
	public void setEyeHeight(int eyeHeight)
	{
		this.eyeHeight = eyeHeight;
	}

	/**
	 * Eye space
	 * 
	 * @return eye space
	 */
	public int getEyeSpace()
	{
		return eyeSpace;
	}

	/**
	 * Eye space
	 * 
	 * @param eyeSpace
	 *            someting connected to eyes
	 */
	public void setEyeSpace(int eyeSpace)
	{
		this.eyeSpace = eyeSpace;
	}

	/**
	 * Returns eye width
	 * 
	 * @return eye width
	 */
	public int getEyeWidth()
	{
		return eyeWidth;
	}

	/**
	 * Sets eye width
	 * 
	 * @param eyeWidth
	 *            eye width
	 */
	public void setEyeWidth(int eyeWidth)
	{
		this.eyeWidth = eyeWidth;
	}

	/**
	 * Returns eye size. Hentai girls usually have very big eyes
	 * 
	 * @return eyes
	 */
	public int getEyeSize()
	{
		return eyeSize;
	}

	/**
	 * Set's eye size.<br>
	 * Can be . o O ;)
	 * 
	 * @param eyeSize
	 *            eye size,
	 */
	public void setEyeSize(int eyeSize)
	{
		this.eyeSize = eyeSize;
	}

	/**
	 * Return eye shape
	 * 
	 * @return eye shape
	 */
	public int getEyeShape()
	{
		return eyeShape;
	}

	/**
	 * Sets Eye shape.<br>
	 * Can be . _ | 0 o O etc :)
	 * 
	 * @param eyeShape
	 *            eye shape
	 */
	public void setEyeShape(int eyeShape)
	{
		this.eyeShape = eyeShape;
	}

	/**
	 * Return eye angle
	 * 
	 * @return eye angle
	 */
	public int getEyeAngle()
	{
		return eyeAngle;
	}

	/**
	 * Sets eye angle, / | \.
	 * 
	 * @param eyeAngle
	 *            eye angle
	 */
	public void setEyeAngle(int eyeAngle)
	{
		this.eyeAngle = eyeAngle;
	}

	/**
	 * Rerturn brow heigth
	 * 
	 * @return brow heigth
	 */
	public int getBrowHeight()
	{
		return browHeight;
	}

	/**
	 * Brow heigth
	 * 
	 * @param browHeight
	 *            brow heigth
	 */
	public void setBrowHeight(int browHeight)
	{
		this.browHeight = browHeight;
	}

	/**
	 * Returns brow angle
	 * 
	 * @return brow angle
	 */
	public int getBrowAngle()
	{
		return browAngle;
	}

	/**
	 * Sets brow angle
	 * 
	 * @param browAngle
	 *            brow angle
	 */
	public void setBrowAngle(int browAngle)
	{
		this.browAngle = browAngle;
	}

	/**
	 * Returns brow shape
	 * 
	 * @return brow shape
	 */
	public int getBrowShape()
	{
		return browShape;
	}

	/*******************************************************************************************************************
	 * Sets brow shape
	 * 
	 * @param browShape
	 *            brow shape
	 */
	public void setBrowShape(int browShape)
	{
		this.browShape = browShape;
	}

	/**
	 * Returns nose
	 * 
	 * @return nose
	 */
	public int getNose()
	{
		return nose;
	}

	/**
	 * Sets nose
	 * 
	 * @param nose
	 *            nose
	 */
	public void setNose(int nose)
	{
		this.nose = nose;
	}

	/**
	 * Returns nose bridge
	 * 
	 * @return nose bridge
	 */
	public int getNoseBridge()
	{
		return noseBridge;
	}

	/**
	 * Sets nose bridge
	 * 
	 * @param noseBridge
	 *            nose bridge
	 */
	public void setNoseBridge(int noseBridge)
	{
		this.noseBridge = noseBridge;
	}

	/**
	 * Returns nose width
	 * 
	 * @return nose width
	 */
	public int getNoseWidth()
	{
		return noseWidth;
	}

	/**
	 * Sets nose width
	 * 
	 * @param noseWidth
	 *            nose width
	 */
	public void setNoseWidth(int noseWidth)
	{
		this.noseWidth = noseWidth;
	}

	/**
	 * Returns noce tip
	 * 
	 * @return noce tip
	 */
	public int getNoseTip()
	{
		return noseTip;
	}

	/**
	 * Sets noce tip
	 * 
	 * @param noseTip
	 *            noce tip
	 */
	public void setNoseTip(int noseTip)
	{
		this.noseTip = noseTip;
	}

	/**
	 * Returns cheeks
	 * 
	 * @return cheeks
	 */
	public int getCheek()
	{
		return cheek;
	}

	/**
	 * Sets cheeks
	 * 
	 * @param cheek
	 *            checks
	 */
	public void setCheek(int cheek)
	{
		this.cheek = cheek;
	}

	/**
	 * Returns lip heigth
	 * 
	 * @return lip heigth
	 */
	public int getLipHeight()
	{
		return lipHeight;
	}

	/**
	 * Sets lip heigth
	 * 
	 * @param lipHeight
	 *            lip heith
	 */
	public void setLipHeight(int lipHeight)
	{
		this.lipHeight = lipHeight;
	}

	/**
	 * Returns mouth size
	 * 
	 * @return mouth size
	 */
	public int getMouthSize()
	{
		return mouthSize;
	}

	/**
	 * Sets mouth size
	 * 
	 * @param mouthSize
	 *            mouth size
	 */
	public void setMouthSize(int mouthSize)
	{
		this.mouthSize = mouthSize;
	}

	/**
	 * Returns lips size
	 * 
	 * @return lips size
	 */
	public int getLipSize()
	{
		return lipSize;
	}

	/**
	 * Sets lips size
	 * 
	 * @param lipSize
	 *            lips size
	 */
	public void setLipSize(int lipSize)
	{
		this.lipSize = lipSize;
	}

	/**
	 * Returns smile
	 * 
	 * @return smile
	 */
	public int getSmile()
	{
		return smile;
	}

	/**
	 * Sets smile
	 * 
	 * @param smile
	 *            smile
	 */
	public void setSmile(int smile)
	{
		this.smile = smile;
	}

	/**
	 * Returns lips shape
	 * 
	 * @return lips shape
	 */
	public int getLipShape()
	{
		return lipShape;
	}

	/**
	 * Sets lips shape
	 * 
	 * @param lipShape
	 *            lips shape
	 */
	public void setLipShape(int lipShape)
	{
		this.lipShape = lipShape;
	}

	/**
	 * Returns jaws height
	 * 
	 * @return jaws height
	 */
	public int getJawHeigh()
	{
		return jawHeigh;
	}

	/**
	 * Sets jaws height
	 * 
	 * @param jawHeigh
	 *            jaws height
	 */
	public void setJawHeigh(int jawHeigh)
	{
		this.jawHeigh = jawHeigh;
	}

	/**
	 * Returns chin jut
	 * 
	 * @return chin jut
	 */
	public int getChinJut()
	{
		return chinJut;
	}

	/**
	 * Sets chin jut
	 * 
	 * @param chinJut
	 *            chin jut
	 */
	public void setChinJut(int chinJut)
	{
		this.chinJut = chinJut;
	}

	/**
	 * Returns ear shape
	 * 
	 * @return ear shape
	 */
	public int getEarShape()
	{
		return earShape;
	}

	/**
	 * Sets ear shape
	 * 
	 * @param earShape
	 *            ear shape
	 */
	public void setEarShape(int earShape)
	{
		this.earShape = earShape;
	}

	/**
	 * Returns head size
	 * 
	 * @return head size
	 */
	public int getHeadSize()
	{
		return headSize;
	}

	/**
	 * Sets head size
	 * 
	 * @param headSize
	 *            head size
	 */
	public void setHeadSize(int headSize)
	{
		this.headSize = headSize;
	}

	/**
	 * Returns neck
	 * 
	 * @return neck
	 */
	public int getNeck()
	{
		return neck;
	}

	/**
	 * Sets neck
	 * 
	 * @param neck
	 *            neck
	 */
	public void setNeck(int neck)
	{
		this.neck = neck;
	}

	/**
	 * Returns neck length
	 * 
	 * @return neck length
	 */
	public int getNeckLength()
	{
		return neckLength;
	}

	/**
	 * Sets neck length, just curious, is it possible to create a giraffe?
	 * 
	 * @param neckLength
	 *            neck length
	 */
	public void setNeckLength(int neckLength)
	{
		this.neckLength = neckLength;
	}

	/**
	 * Shoulders
	 * 
	 * @return shouldeers
	 */
	public int getShoulders()
	{
		return shoulders;
	}

	/**
	 * Shoulders
	 * 
	 * @param shoulders
	 *            shoulders
	 */
	public void setShoulders(int shoulders)
	{
		this.shoulders = shoulders;
	}

	/**
	 * Torso
	 * 
	 * @return torso
	 */
	public int getTorso()
	{
		return torso;
	}

	/**
	 * Sets torso
	 * 
	 * @param torso
	 *            torso
	 */
	public void setTorso(int torso)
	{
		this.torso = torso;
	}

	/**
	 * Returns tits
	 * 
	 * @return tits
	 */
	public int getChest()
	{
		return chest;
	}

	/**
	 * Sets tits
	 * 
	 * @param chest
	 *            tits
	 */
	public void setChest(int chest)
	{
		this.chest = chest;
	}

	/**
	 * Returns waist
	 * 
	 * @return waist
	 */
	public int getWaist()
	{
		return waist;
	}

	/**
	 * sets waist
	 * 
	 * @param waist
	 *            waist
	 */
	public void setWaist(int waist)
	{
		this.waist = waist;
	}

	/**
	 * Returns hips
	 * 
	 * @return hips
	 */
	public int getHips()
	{
		return hips;
	}

	/**
	 * Sets hips
	 * 
	 * @param hips
	 *            hips
	 */
	public void setHips(int hips)
	{
		this.hips = hips;
	}

	/**
	 * Returns arm thickness
	 * 
	 * @return arm thickness
	 */
	public int getArmThickness()
	{
		return armThickness;
	}

	/**
	 * Sets arm thickness
	 * 
	 * @param armThickness
	 *            arm thickness
	 */
	public void setArmThickness(int armThickness)
	{
		this.armThickness = armThickness;
	}

	/**
	 * Returns hand size
	 * 
	 * @return hand size
	 */
	public int getHandSize()
	{
		return handSize;
	}

	/**
	 * Sets hand size
	 * 
	 * @param handSize
	 *            hand size
	 */
	public void setHandSize(int handSize)
	{
		this.handSize = handSize;
	}

	/**
	 * Returns legs thickness
	 * 
	 * @return leg thickness
	 */
	public int getLegThicnkess()
	{
		return legThicnkess;
	}

	/**
	 * Sets leg thickness
	 * 
	 * @param legThicnkess
	 *            leg thickness
	 */
	public void setLegThicnkess(int legThicnkess)
	{
		this.legThicnkess = legThicnkess;
	}

	/**
	 * Returns foot size
	 * 
	 * @return foot size
	 */
	public int getFootSize()
	{
		return footSize;
	}

	/**
	 * Sets foot size
	 * 
	 * @param footSize
	 *            foot size
	 */
	public void setFootSize(int footSize)
	{
		this.footSize = footSize;
	}

	/**
	 * Retunrs facial rate
	 * 
	 * @return facial rate
	 */
	public int getFacialRate()
	{
		return facialRate;
	}

	/**
	 * Sets facial rate
	 * 
	 * @param facialRate
	 *            facial rate
	 */
	public void setFacialRate(int facialRate)
	{
		this.facialRate = facialRate;
	}

	/**
	 * Returns sexy voice
	 * 
	 * @return sexy voice
	 */
	public int getVoice()
	{
		return voice;
	}

	/**
	 * Sets sexy voice
	 * 
	 * @param voice
	 *            sexy voice
	 */
	public void setVoice(int voice)
	{
		this.voice = voice;
	}

	/**
	 * Returns height
	 * 
	 * @return height
	 */
	public float getHeight()
	{
		return height;
	}

	/**
	 * Sets height
	 * 
	 * @param height
	 *            height
	 */
	public void setHeight(float height)
	{
		this.height = height;
	}

	// Hurrrraaaaay! it's the end of the class :D :D :D :D :D
}
