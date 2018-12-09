package com.actelion.research.gwt.minimal;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import com.actelion.research.chem.*;
import com.actelion.research.chem.contrib.*;
import com.actelion.research.chem.coords.CoordinateInventor;
import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.*;

@JsType(name = "Molecule")
public class JSMolecule {
	
	private static Services services = Services.getInstance();
	private static Rectangle2D.Double rectangle = new Rectangle2D.Double();
	
	private StereoMolecule oclMolecule;

	public JSMolecule(int maxAtoms, int maxBonds, StereoMolecule mol) {
		if (mol != null) {
			oclMolecule = mol;
		} else {
			oclMolecule = new StereoMolecule(maxAtoms, maxBonds);
		}
	}

	@JsIgnore
	public JSMolecule(StereoMolecule mol) {
		this(0, 0, mol);
	}

	@JsIgnore
	public JSMolecule() {
	  this(32, 32, null);
	}
	
	public static native JSMolecule fromSmiles(String smiles, JavaScriptObject options) throws Exception
	/*-{
		options = options || {};
		var coordinates = !options.noCoordinates;
		var stereo = !options.noStereo;
		return @com.actelion.research.gwt.minimal.JSMolecule::fromSmiles(Ljava/lang/String;ZZ)(smiles, coordinates, stereo);
	}-*/;
	
	public static JSMolecule fromMolfile(String molfile) throws Exception {
		return new JSMolecule(new MolfileParser().getCompactMolecule(molfile));
	}

	public static JavaScriptObject fromMolfileWithAtomMap(String molfile) throws Exception {
		MolfileParser parser = new MolfileParser(MolfileParser.MODE_KEEP_HYDROGEN_MAP);
		StereoMolecule mol = parser.getCompactMolecule(molfile);
		int[] map = parser.getHandleHydrogenMap();
		return createMolfileWithAtomMap(new JSMolecule(mol), map);
	}

	private static native JavaScriptObject createMolfileWithAtomMap(JSMolecule mol, int[] map)
	/*-{
		return {molecule: mol, map: map};
	}-*/;
	
	public static native JSMolecule fromIDCode(String idcode, JavaScriptObject coordinates)
	/*-{
		var mol;
		if (typeof coordinates === 'undefined') {
			coordinates = true;
		}
		if (typeof coordinates === 'boolean') {
			mol = @com.actelion.research.gwt.minimal.JSMolecule::fromIDCode(Ljava/lang/String;Z)(idcode, false);
			if (coordinates === true) {
				mol.@com.actelion.research.gwt.minimal.JSMolecule::inventCoordinates()();
			}
		} else if(typeof coordinates === 'string') {
			mol = @com.actelion.research.gwt.minimal.JSMolecule::fromIDCode(Ljava/lang/String;Ljava/lang/String;)(idcode, coordinates);
		}
		return mol;
	}-*/;
	
	public String toSmiles() {
		return new SmilesCreator().generateSmiles(oclMolecule);
	}

	public String toIsomericSmiles(boolean includeAtomMapping) {
		return new IsomericSmilesCreator(oclMolecule, includeAtomMapping).getSmiles();
	}
	
	public String toMolfile() {
		MolfileCreator creator = new MolfileCreator(oclMolecule);
		return creator.getMolfile();
	}

	public String toMolfileV3() {
		MolfileV3Creator creator = new MolfileV3Creator(oclMolecule);
		return creator.getMolfile();
	}

	public native String toSVG(int width, int height, String id, JavaScriptObject options)
	/*-{
		//todo: re-enable this check once it becomes possible to change the font
		//if (!$doc.createElement) {
		//	throw new Error('Molecule#toSVG cannot be used outside of a browser\'s Window environment');
		//}
		options = options || {};
		var factorTextSize = options.factorTextSize || 1;
		var svg =  this.@com.actelion.research.gwt.minimal.JSMolecule::getSVG(IIFLjava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(width, height, factorTextSize, id, options);
		if (options.fontWeight) {
            		svg = svg.replace(/font-family=" Helvetica" /g, 'font-family=" Helvetica" font-weight="' + options.fontWeight + '" ');
        	}
        	if (options.strokeWidth) {
            		svg = svg.replace(/stroke-width:1/g, 'stroke-width:' + options.strokeWidth + ' ');
        	}
        	return svg;
	}-*/;
	
	private String getSVG(int width, int height, float factorTextSize, String id, JavaScriptObject options) {
		int mode = Util.getDisplayMode(options);
		SVGDepictor d = new SVGDepictor(oclMolecule, mode, id);
		d.setFactorTextSize(factorTextSize);
		d.validateView(null, new Rectangle2D.Double(0, 0, width, height), AbstractDepictor.cModeInflateToMaxAVBL);
		d.paint(null);
		return d.toString();
	}
	
	public String getCanonizedIDCode(int flag) {
		Canonizer canonizer = new Canonizer(oclMolecule, flag);
		return canonizer.getIDCode();
	}

	public native JavaScriptObject getIDCodeAndCoordinates()
	/*-{
		return {
			idCode: this.@com.actelion.research.gwt.minimal.JSMolecule::getIDCode()(),
			coordinates: this.@com.actelion.research.gwt.minimal.JSMolecule::getIDCoordinates()()
		};
	}-*/;

	public MolecularFormula getMolecularFormula() {
		return new MolecularFormula(oclMolecule);
	}
	
	public int[] getIndex() {
		return services.getSSSearcherWithIndex().createIndex(oclMolecule);
	}
	
	public void inventCoordinates() {
		CoordinateInventor inventor = new CoordinateInventor();
		inventor.setRandomSeed(0);
		inventor.invent(oclMolecule);
		oclMolecule.setStereoBondsFromParity();
	}

	public native void addImplicitHydrogens(JavaScriptObject atomNumber)
	/*-{
		if (atomNumber === undefined) {
			this.@com.actelion.research.gwt.minimal.JSMolecule::addImplicitHydrogens()();
		} else {
			this.@com.actelion.research.gwt.minimal.JSMolecule::addImplicitHydrogens(I)(atomNumber);
		}
	}-*/;

	public int getNumberOfHydrogens() {
		return HydrogenHandler.getNumberOfHydrogens(oclMolecule);
	}

	public String[] getDiastereotopicAtomIDs() {
		return DiastereotopicAtomID.getAtomIds(oclMolecule);
	}

		public native void addMissingChirality(JavaScriptObject esrType)
		/*-{
        if (esrType === undefined) {
            this.@com.actelion.research.gwt.minimal.JSMolecule::addMissingChirality()();
        } else {
            this.@com.actelion.research.gwt.minimal.JSMolecule::addMissingChirality(I)(esrType);
        }
    }-*/;

    @JsIgnore
	public void addMissingChirality() {
		DiastereotopicAtomID.addMissingChirality(oclMolecule);
	}

    @JsIgnore
    public void addMissingChirality(int esrType) {
        DiastereotopicAtomID.addMissingChirality(oclMolecule, esrType);
    }

	public native String[][] getHoseCodes(JavaScriptObject options)
	/*-{
		options = options || {};
		var maxSphereSize = (typeof options.maxSphereSize === 'undefined' ? 5 : options.maxSphereSize) | 0;
		var type = (typeof options.type === 'undefined' ? 0 : options.type) | 0;
		return @com.actelion.research.chem.contrib.HoseCodeCreator::getHoseCodes(Lcom/actelion/research/chem/StereoMolecule;II)(this.@com.actelion.research.gwt.minimal.JSMolecule::oclMolecule, maxSphereSize, type);
	}-*/;

	/**
	 * @return a RingCollection object, which contains a total set of small rings
	 */
	public JSRingCollection getRingSet() {
		return new JSRingCollection(oclMolecule.getRingSet());
	}

	public JavaScriptObject getBounds() {
		Rectangle2D.Double r = oclMolecule.getBounds(rectangle);
		if (r == null) return null;
		return getBounds(r.x, r.y, r.width, r.height);
	}
	
	/* public methods after this line will not be accessible from javascript */

	private native JavaScriptObject getBounds(double x, double y, double width, double height)
	/*-{
		return { x: x, y: y, width: width, height: height };
	}-*/;

	private void addImplicitHydrogens() {
		HydrogenHandler.addImplicitHydrogens(oclMolecule);
	}

	private void addImplicitHydrogens(int atomNumber) {
		HydrogenHandler.addImplicitHydrogens(oclMolecule, atomNumber);
	}
	
	@JsIgnore
	public static JSMolecule fromSmiles(String smiles, boolean ensure2DCoordinates, boolean readStereoFeatures) throws Exception {
		JSMolecule mol = new JSMolecule();
		new SmilesParser().parse(mol.oclMolecule, smiles.getBytes(), false, readStereoFeatures);
		if (ensure2DCoordinates) {
			mol.inventCoordinates();
		}
		return mol;
	}
	
	@JsIgnore
	public static JSMolecule fromIDCode(String idcode, boolean ensure2DCoordinates) {
		return new JSMolecule(new IDCodeParser(ensure2DCoordinates).getCompactMolecule(idcode));
	}
	
	@JsIgnore
	public static JSMolecule fromIDCode(String idcode, String coordinates) {
		return new JSMolecule(new IDCodeParser(false).getCompactMolecule(idcode, coordinates));
	}

	@JsIgnore
	public StereoMolecule getStereoMolecule() {
		return oclMolecule;
	}
	public static final int CANONIZER_CREATE_SYMMETRY_RANK = 1;
	public static final int CANONIZER_CONSIDER_DIASTEREOTOPICITY = 2;
	public static final int CANONIZER_CONSIDER_ENANTIOTOPICITY = 4;
	public static final int CANONIZER_CONSIDER_STEREOHETEROTOPICITY = CANONIZER_CONSIDER_DIASTEREOTOPICITY | CANONIZER_CONSIDER_ENANTIOTOPICITY;
	public static final int CANONIZER_ENCODE_ATOM_CUSTOM_LABELS = 8;
	public static final int CANONIZER_ENCODE_ATOM_SELECTION = 16;
	public static final int CANONIZER_ASSIGN_PARITIES_TO_TETRAHEDRAL_N = 32;
	public static final int CANONIZER_COORDS_ARE_3D = 64;
	public static final int CANONIZER_CREATE_PSEUDO_STEREO_GROUPS = 128;
	public static final int CANONIZER_DISTINGUISH_RACEMIC_OR_GROUPS = 256;

	// GENERATED AUTOMATICALLY DO NOT EDIT AFTER THIS LINE
public static final int cMaxAtomicNo = 190;
public static final int cAtomParityNone			= 0x000000;
public static final int cAtomParity1			= 0x000001;
public static final int cAtomParity2			= 0x000002;
public static final int cAtomParityUnknown		= 0x000003;
public static final int cAtomParityIsPseudo		= 0x000004;
public static final int cAtomRadicalState		= 0x000030;
public static final int cAtomRadicalStateShift	= 4;
public static final int cAtomRadicalStateNone	= 0x000000;
public static final int cAtomRadicalStateS		= 0x000010;
public static final int cAtomRadicalStateD		= 0x000020;
public static final int cAtomRadicalStateT		= 0x000030;
public static final int cAtomColorNone			= 0x000000;
public static final int cAtomColorBlue			= 0x000040;
public static final int cAtomColorRed			= 0x000080;
public static final int cAtomColorGreen			= 0x0000C0;
public static final int cAtomColorMagenta		= 0x000100;
public static final int cAtomColorOrange		= 0x000140;
public static final int cAtomColorDarkGreen		= 0x000180;
public static final int cAtomColorDarkRed		= 0x0001C0;
public static final int cAtomCIPParityNone		= 0x000000;
public static final int cAtomCIPParityRorM		= 0x000001;
public static final int cAtomCIPParitySorP		= 0x000002;
public static final int cAtomCIPParityProblem	= 0x000003;
public static final int cESRTypeAbs				= 0;
public static final int cESRTypeAnd				= 1;
public static final int cESRTypeOr				= 2;
public static final int cESRMaxGroups			= 32;
public static final int cESRGroupBits			= 5;
public static final int cAtomQFNoOfBits			= 30;
public static final int cAtomQFAromStateBits	= 2;
public static final int cAtomQFAromStateShift	= 1;
public static final int cAtomQFRingStateBits	= 4;
public static final int cAtomQFRingStateShift	= 3;
public static final int cAtomQFHydrogenBits		= 4;
public static final int cAtomQFHydrogenShift	= 7;
public static final int cAtomQFPiElectronBits	= 3;
public static final int cAtomQFPiElectronShift	= 14;
public static final int cAtomQFNeighbourBits	= 5;
public static final int cAtomQFNeighbourShift	= 17;
public static final int cAtomQFRingSizeBits		= 3;
public static final int cAtomQFRingSizeShift	= 22;
public static final int cAtomQFChargeBits		= 3;
public static final int cAtomQFChargeShift		= 25;
public static final int cAtomQFSimpleFeatures	= 0x0E3FC7FE;
public static final int cAtomQFNarrowing		= 0x0E3FC7FE;
public static final int cAtomQFAny				= 0x00000001;
public static final int cAtomQFAromState		= 0x00000006;
public static final int cAtomQFAromatic			= 0x00000002;
public static final int cAtomQFNotAromatic		= 0x00000004;
public static final int cAtomQFRingState		= 0x00000078;
public static final int cAtomQFNotChain			= 0x00000008;
public static final int cAtomQFNot2RingBonds	= 0x00000010;
public static final int cAtomQFNot3RingBonds	= 0x00000020;
public static final int cAtomQFNot4RingBonds	= 0x00000040;
public static final int cAtomQFHydrogen			= 0x00000780;
public static final int cAtomQFNot0Hydrogen		= 0x00000080;
public static final int cAtomQFNot1Hydrogen		= 0x00000100;
public static final int cAtomQFNot2Hydrogen		= 0x00000200;
public static final int cAtomQFNot3Hydrogen		= 0x00000400;
public static final int cAtomQFNoMoreNeighbours	= 0x00000800;
public static final int cAtomQFMoreNeighbours	= 0x00001000;
public static final int cAtomQFMatchStereo		= 0x00002000;
public static final int cAtomQFPiElectrons		= 0x0001C000;
public static final int cAtomQFNot0PiElectrons  = 0x00004000;
public static final int cAtomQFNot1PiElectron   = 0x00008000;
public static final int cAtomQFNot2PiElectrons  = 0x00010000;
public static final int cAtomQFNeighbours		= 0x003E0000;  // these QF refer to non-H neighbours
public static final int cAtomQFNot0Neighbours   = 0x00020000;
public static final int cAtomQFNot1Neighbour	= 0x00040000;
public static final int cAtomQFNot2Neighbours   = 0x00080000;
public static final int cAtomQFNot3Neighbours   = 0x00100000;
public static final int cAtomQFNot4Neighbours   = 0x00200000;  // this is not 4 or more neighbours
public static final int cAtomQFRingSize			= 0x01C00000;
public static final int cAtomQFCharge			= 0x0E000000;
public static final int cAtomQFNotChargeNeg		= 0x02000000;
public static final int cAtomQFNotCharge0		= 0x04000000;
public static final int cAtomQFNotChargePos		= 0x08000000;
public static final int cAtomQFFlatNitrogen		= 0x10000000;  // currently only used in TorsionDetail
public static final int cAtomQFExcludeGroup		= 0x20000000;  // these atoms must not exist in SS-matches
public static final int cBondTypeSingle			= 0x00000001;
public static final int cBondTypeDouble			= 0x00000002;
public static final int cBondTypeTriple			= 0x00000004;
public static final int cBondTypeDown			= 0x00000009;
public static final int cBondTypeUp				= 0x00000011;
public static final int cBondTypeCross			= 0x0000001A;
public static final int cBondTypeMetalLigand	= 0x00000020;
public static final int cBondTypeDelocalized	= 0x00000040;
public static final int cBondTypeDeleted		= 0x00000080;
public static final int cBondTypeIncreaseOrder  = 0x0000007F;
public static final int cBondParityNone			= 0x00000000;
public static final int cBondParityEor1			= 0x00000001;
public static final int cBondParityZor2			= 0x00000002;
public static final int cBondParityUnknown		= 0x00000003;
public static final int cBondCIPParityNone		= 0x00000000;
public static final int cBondCIPParityEorP		= 0x00000001;
public static final int cBondCIPParityZorM		= 0x00000002;
public static final int cBondCIPParityProblem   = 0x00000003;
public static final int cBondQFNoOfBits			= 21;
public static final int cBondQFBondTypesBits	= 5;
public static final int cBondQFBondTypesShift	= 0;
public static final int cBondQFRingStateBits	= 2;
public static final int cBondQFRingStateShift	= 5;
public static final int cBondQFBridgeBits		= 8;
public static final int cBondQFBridgeShift		= 7;
public static final int cBondQFBridgeMinBits	= 4;
public static final int cBondQFBridgeMinShift   = 7;
public static final int cBondQFBridgeSpanBits   = 4;
public static final int cBondQFBridgeSpanShift  = 11;
public static final int cBondQFRingSizeBits		= 3;
public static final int cBondQFRingSizeShift	= 15;
public static final int cBondQFAromStateBits	= 2;
public static final int cBondQFAromStateShift	= 19;
public static final int cBondQFAllFeatures		= 0x001FFFFF;
public static final int cBondQFSimpleFeatures	= 0x0018007F;
public static final int cBondQFNarrowing		= 0x00180060;
public static final int cBondQFBondTypes		= 0x0000001F;
public static final int cBondQFSingle			= 0x00000001;
public static final int cBondQFDouble			= 0x00000002;
public static final int cBondQFTriple			= 0x00000004;
public static final int cBondQFDelocalized		= 0x00000008;
public static final int cBondQFMetalLigand		= 0x00000010;
public static final int cBondQFRingState		= 0x00000060;
public static final int cBondQFNotRing			= 0x00000020;
public static final int cBondQFRing				= 0x00000040;
public static final int cBondQFBridge			= 0x00007F80;
public static final int cBondQFBridgeMin		= 0x00000780;
public static final int cBondQFBridgeSpan		= 0x00007800;
public static final int cBondQFRingSize			= 0x00038000;
public static final int cBondQFMatchStereo		= 0x00040000;
public static final int cBondQFAromState		= 0x00180000;
public static final int cBondQFAromatic			= 0x00080000;
public static final int cBondQFNotAromatic		= 0x00100000;
public static final int cHelperNone				= 0x0000;
public static final int cHelperBitNeighbours	= 0x0001;
public static final int cHelperBitRings			= 0x0002;
public static final int cHelperBitParities		= 0x0004;
public static final int cHelperBitCIP			= 0x0008;
public static final int cHelperBitSymmetrySimple			= 0x0010;
public static final int cHelperBitSymmetryDiastereotopic	= 0x0020;
public static final int cHelperBitSymmetryEnantiotopic		= 0x0040;
public static final int cHelperBitIncludeNitrogenParities	= 0x0080;
public static final int cHelperBitsStereo = 0x00FC;
public static final int cHelperNeighbours = cHelperBitNeighbours;
public static final int cHelperRings = cHelperNeighbours | cHelperBitRings;
public static final int cHelperParities = cHelperRings | cHelperBitParities;
public static final int cHelperCIP = cHelperParities | cHelperBitCIP;
public static final int cHelperSymmetrySimple = cHelperCIP | cHelperBitSymmetrySimple;
public static final int cHelperSymmetryDiastereotopic = cHelperCIP | cHelperBitSymmetryDiastereotopic;
public static final int cHelperSymmetryEnantiotopic = cHelperCIP | cHelperBitSymmetryEnantiotopic;
public static final int cChiralityIsomerCountMask   = 0x00FFFF;
public static final int cChiralityUnknown		  	= 0x000000;
public static final int cChiralityNotChiral			= 0x010000;
public static final int cChiralityMeso				= 0x020000; // this has added the number of meso isomers
public static final int cChiralityRacemic			= 0x030000;
public static final int cChiralityKnownEnantiomer   = 0x040000;
public static final int cChiralityUnknownEnantiomer = 0x050000;
public static final int cChiralityEpimers		 	= 0x060000;
public static final int cChiralityDiastereomers		= 0x070000; // this has added the number of diastereomers
	public static final int cMoleculeColorDefault = 0;
	public static final int cMoleculeColorNeutral = 1;
public static final String cAtomLabel[] = { "?",
	"H"  ,"He" ,"Li" ,"Be" ,"B"  ,"C"  ,"N"  ,"O"  ,
	"F"  ,"Ne" ,"Na" ,"Mg" ,"Al" ,"Si" ,"P"  ,"S"  ,
	"Cl" ,"Ar" ,"K"  ,"Ca" ,"Sc" ,"Ti" ,"V"  ,"Cr" ,
	"Mn" ,"Fe" ,"Co" ,"Ni" ,"Cu" ,"Zn" ,"Ga" ,"Ge" ,
	"As" ,"Se" ,"Br" ,"Kr" ,"Rb" ,"Sr" ,"Y"  ,"Zr" ,
	"Nb" ,"Mo" ,"Tc" ,"Ru" ,"Rh" ,"Pd" ,"Ag" ,"Cd" ,
	"In" ,"Sn" ,"Sb" ,"Te" ,"I"  ,"Xe" ,"Cs" ,"Ba" ,
	"La" ,"Ce" ,"Pr" ,"Nd" ,"Pm" ,"Sm" ,"Eu" ,"Gd" ,
	"Tb" ,"Dy" ,"Ho" ,"Er" ,"Tm" ,"Yb" ,"Lu" ,"Hf" ,
	"Ta" ,"W"  ,"Re" ,"Os" ,"Ir" ,"Pt" ,"Au" ,"Hg" ,
	"Tl" ,"Pb" ,"Bi" ,"Po" ,"At" ,"Rn" ,"Fr" ,"Ra" ,
	"Ac" ,"Th" ,"Pa" ,"U"  ,"Np" ,"Pu" ,"Am" ,"Cm" ,
	"Bk" ,"Cf" ,"Es" ,"Fm" ,"Md" ,"No" ,"Lr" ,"??" ,
	"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,
	"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,
	"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,
	"R4" ,"R5" ,"R6" ,"R7" ,"R8" ,"R9" ,"R10","R11",	// R4 to R16 do not belong to the MDL set
	"R12","R13","R14","R15","R16","R1" ,"R2" ,"R3" ,
	"A"  ,"A1" ,"A2" ,"A3" ,"??" ,"??" ,"D"  ,"T"  ,
	"X"  ,"R"  ,"H2" ,"H+" ,"Nnn","HYD","Pol","??" ,
	"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,"??" ,
	"??" ,"??" ,"Ala","Arg","Asn","Asp","Cys","Gln",
	"Glu","Gly","His","Ile","Leu","Lys","Met","Phe",
	"Pro","Ser","Thr","Trp","Tyr","Val" };
public static final short cRoundedMass[] = { 0,
	1,	  4,	  7,	  9,	 11,	 12,   //  H  ,He ,Li ,Be ,B  ,C  ,
	14,	 16,	 19,	 20,	 23,	 24,   //  N , O  ,F  ,Ne ,Na ,Mg ,
	27,	 28,	 31,	 32,	 35,	 40,   //  Al ,Si ,P  ,S  ,Cl ,Ar ,
	39,	 40,	 45,	 48,	 51,	 52,   //  K  ,Ca ,Sc ,Ti ,V  ,Cr ,
	55,	 56,	 59,	 58,	 63,	 64,   //  Mn ,Fe ,Co ,Ni ,Cu ,Zn ,
	69,	 74,	 75,	 80,	 79,	 84,   //  Ga ,Ge ,As ,Se ,Br ,Kr ,
	85,	 88,	 89,	 90,	 93,	 98,   //  Rb ,Sr ,Y  ,Zr ,Nb ,Mo ,
	0,	102,	103,	106,	107,	114,   //  Tc ,Ru ,Rh ,Pd ,Ag ,Cd ,
	115,	120,	121,	130,	127,	132,   //  In ,Sn ,Sb ,Te ,I  ,Xe ,
	133,	138,	139,	140,	141,	142,   //  Cs ,Ba ,La ,Ce ,Pr ,Nd ,
	0,	152,	153,	158,	159,	164,   //  Pm ,Sm ,Eu ,Gd ,Tb ,Dy ,
	165,	166,	169,	174,	175,	180,   //  Ho ,Er ,Tm ,Yb ,Lu ,Hf ,
	181,	184,	187,	192,	193,	195,   //  Ta ,W , Re ,Os ,Ir ,Pt ,
	197,	202,	205,	208,	209,	  0,   //  Au ,Hg ,Tl ,Pb ,Bi ,Po ,
	0,	  0,	  0,	  0,	  0,	232,   //  At ,Rn ,Fr ,Ra ,Ac ,Th ,
	0,	238,	  0,	  0,	  0,	  0,   //  Pa ,U , Np ,Pu ,Am ,Cm ,
	0,	  0,	  0,	  0,	  0,	  0,   //  Bk ,Cf ,Es ,Fm ,Md ,No ,
	0,	  0,	  0,	  0,	  0,	  0,   //  Lr ,?? ,?? ,?? ,?? ,?? ,
	0,	  0,	  0,	  0,	  0,	  0,   //  ?? ,?? ,?? ,?? ,?? ,?? ,
	0,	  0,	  0,	  0,	  0,	  0,   //  ?? ,?? ,?? ,?? ,?? ,?? ,
	0,	  0,	  0,	  0,	  0,	  0,   //  ?? ,?? ,?? ,?? ,?? ,?? ,
	0,	  0,	  0,	  0,	  0,	  0,   //  ?? ,?? ,R4 ,R5 ,R6 ,R7 ,
	0,	  0,	  0,	  0,	  0,	  0,   //  R8 ,R9 ,R10,R11,R12,R13,
	0,	  0,	  0,	  0,	  0,	  0,   //  R14,R15,R16,R1 ,R2 ,R3 ,
	0,	  0,	  0,	  0,	  0,	  0,   //  A  ,A1 ,A2 ,A3 ,?? ,?? ,
	2,	  3,	  0,	  0,	  0,	  0,   //  D  ,T  ,X  ,R  ,H2 ,H+
	0,	  0,	  0,	  0,	  0,	  0,   //  Nnn,HYD,Pol,?? ,?? ,?? ,
	0,	  0,	  0,	  0,	  0,	  0,   //  ?? ,?? ,?? ,?? ,?? ,?? ,
	0,	  0,	 71,	156,	114,	115,   //  ?? ,?? ,Ala,Arg,Asn,Asp,
	103,	128,	129,	 57,	137,	113,   //  Cys,Gln,Glu,Gly,His,Ile,
	113,	128,	131,	147,	 97,	 87,   //  Leu,Lys,Met,Phe,Pro,Ser,
	101,	186,	163,	 99 };					//  Thr,Trp,Tyr,Val,
public static final int cDefaultAtomValence = 6;
public static final float FISCHER_PROJECTION_LIMIT = (float)Math.PI / 36;
public static final float STEREO_ANGLE_LIMIT = (float)Math.PI / 36;   // 5 degrees
public static final int cMaxConnAtoms = 16; // ExtendedMolecule is not restricted anymore
public static final String VALIDATION_ERROR_ESR_CENTER_UNKNOWN = "Members of ESR groups must only be stereo centers with known configuration.";
public static final String VALIDATION_ERROR_OVER_UNDER_SPECIFIED = "Over- or under-specified stereo feature or more than one racemic type bond";
public static final String VALIDATION_ERROR_AMBIGUOUS_CONFIGURATION = "Ambiguous configuration at stereo center because of 2 parallel bonds";
public static final String[] VALIDATION_ERRORS_STEREO = {
	VALIDATION_ERROR_ESR_CENTER_UNKNOWN,
	VALIDATION_ERROR_OVER_UNDER_SPECIFIED,
	VALIDATION_ERROR_AMBIGUOUS_CONFIGURATION
	};

public static int getAtomicNoFromLabel(String atomLabel) {
	return StereoMolecule.getAtomicNoFromLabel(atomLabel);
}


public static double getAngle(double x1, double y1, double x2, double y2) {
	return StereoMolecule.getAngle(x1, y1, x2, y2);
}


public static double getAngleDif(double angle1, double angle2) {
	return StereoMolecule.getAngleDif(angle1, angle2);
}

/**
 * High level function for constructing a molecule.
 * @param atomicNo
 * @return
 */
public int addAtom(int atomicNo) {
	return oclMolecule.addAtom(atomicNo);
}

/**
 * Suggests either cBondTypeSingle or cBondTypeMetalLigand
 * whatever seems more appropriate for a new bond between the two atoms.
 * @param atom1
 * @param atom2
 * @return preferred bond type
 */
public int suggestBondType(int atom1, int atom2) {
	return oclMolecule.suggestBondType(atom1, atom2);
}

/**
 * High level function for constructing a molecule.
 * Adds a single or metal bond between the two atoms
 * depending on whether one of them is a metal atom.
 * @param atom1
 * @param atom2
 * @return new bond index
 */
public int addBond(int atom1, int atom2) {
	return oclMolecule.addBond(atom1, atom2);
}

/**
 * High level function for constructing a molecule.
 * @param x
 * @param y
 * @param atomicNo
 * @param mass
 * @param abnormalValence
 * @param radical
 * @return
 */
public boolean addOrChangeAtom(double x, double y, int atomicNo, int mass, int abnormalValence, int radical, String customLabel) {
	return oclMolecule.addOrChangeAtom(x, y, atomicNo, mass, abnormalValence, radical, customLabel);
}

/**
 * High level function for constructing a molecule.
 * @param atm1
 * @param atm2
 * @param type
 * @return
 */
public int addOrChangeBond(int atm1,int atm2,int type) {
	return oclMolecule.addOrChangeBond(atm1,atm2,type);
}

/**
 * High level function for constructing a molecule.
 * @param x
 * @param y
 * @param ringSize
 * @param aromatic
 * @return
 */
public boolean addRing(double x, double y, int ringSize, boolean aromatic) {
	return oclMolecule.addRing(x, y, ringSize, aromatic);
}

/**
 * High level function for constructing a molecule.
 * @param atom
 * @param ringSize
 * @param aromatic
 * @return
 */
public boolean addRingToAtom(int atom, int ringSize, boolean aromatic) {
	return oclMolecule.addRingToAtom(atom, ringSize, aromatic);
}

/**
 * High level function for constructing a molecule.
 * @param bond
 * @param ringSize
 * @param aromatic
 * @return
 */
public boolean addRingToBond(int bond, int ringSize, boolean aromatic) {
	return oclMolecule.addRingToBond(bond, ringSize, aromatic);
}

/**
 * High level function for constructing a molecule.
 * @param atom
 * @param atomicNo
 * @param mass
 * @param abnormalValence
 * @param radical
 * @return
 */
public boolean changeAtom(int atom, int atomicNo, int mass, int abnormalValence, int radical) {
	return oclMolecule.changeAtom(atom, atomicNo, mass, abnormalValence, radical);
}

/**
 * High level function for constructing a molecule.
 * @param atom
 * @param positive
 * @return
 */
public boolean changeAtomCharge(int atom, boolean positive) {
	return oclMolecule.changeAtomCharge(atom, positive);
}

/**
 * High level function for constructing a molecule.
 * @param bnd
 * @param type
 * @return
 */
public boolean changeBond(int bnd, int type) {
	return oclMolecule.changeBond(bnd, type);
}

/**
 * Copies all atoms and bonds of mol to the end of this Molecule's atom and bond
 * tables. If mol is a fragment then this Molecule's fragment flag is set to true
 * and all query features of mol are also copied.
 * High level function for constructing a molecule.
 * @param mol
 * @return atom mapping from original mol to this molecule after incorporation of mol
 */
public int[] addMolecule(JSMolecule mol) {
	return oclMolecule.addMolecule(mol.getStereoMolecule());
}

/**
 * High level function for constructing a molecule.
 * @param substituent
 * @param connectionAtom
 * @return atom mapping from substituent to this molecule after addition of substituent
 */
public int[] addSubstituent(JSMolecule substituent, int connectionAtom) {
	return oclMolecule.addSubstituent(substituent.getStereoMolecule(), connectionAtom);
}

/**
 * Copies this molecule including parity settings, if valid.
 * The original content of destMol is replaced.
 * Helper arrays are not copied and need to be recalculated if needed.
 * @param destMol
 */
public void copyMolecule(JSMolecule destMol) {
	oclMolecule.copyMolecule(destMol.getStereoMolecule());
}

/**
 * Creates a new atom in destMol and copies all source atom properties
 * including atom list, custom label, flags, and mapNo to it.
 * @param destMol
 * @param sourceAtom
 * @param esrGroupOffsetAND -1 to create new ESR group or destMol ESR group count from esrGroupCountAND()
 * @param esrGroupOffsetOR -1 to create new ESR group or destMol ESR group count from esrGroupCountOR()
 * @return index of new atom in destMol
 */
public int copyAtom(JSMolecule destMol, int sourceAtom, int esrGroupOffsetAND, int esrGroupOffsetOR) {
	return oclMolecule.copyAtom(destMol.getStereoMolecule(), sourceAtom, esrGroupOffsetAND, esrGroupOffsetOR);
}

/**
 * @param destMol
 * @param sourceBond
 * @param esrGroupOffsetAND -1 to create new ESR group or destMol ESR group count from esrGroupCountAND()
 * @param esrGroupOffsetOR -1 to create new ESR group or destMol ESR group count from esrGroupCountOR()
 * @param atomMap
 * @param useBondTypeDelocalized
 * @return
 */
public int copyBond(JSMolecule destMol, int sourceBond, int esrGroupOffsetAND, int esrGroupOffsetOR, int[] atomMap, boolean useBondTypeDelocalized) {
	return oclMolecule.copyBond(destMol.getStereoMolecule(), sourceBond, esrGroupOffsetAND, esrGroupOffsetOR, atomMap, useBondTypeDelocalized);
}

/**
 * Copies name,isFragment,chirality and validity of parity & CIP flags.
 * When copying molecules parts only or when changing the atom order during copy,
 * then atom parities or CIP parities may not be valid anymore and
 * invalidateHelperArrays([affected bits]) should be called in these cases.
 * @param destMol
 */
public void copyMoleculeProperties(JSMolecule destMol) {
		oclMolecule.copyMoleculeProperties(destMol.getStereoMolecule());
}

/**
 * Clears helperBits from mValidHelperArrays.
 * @param helperBits
 */
public void invalidateHelperArrays(int helperBits) {
	oclMolecule.invalidateHelperArrays(helperBits);
}

/**
 * For the given ESR type (AND or OR) renumbers all group indexes starting from 0.
 * Use this, if stereo center deletion or other operations caused an inconsisten ESR
 * number state. Molecule and derived methods do this automatically.
 * @param type cESRTypeAnd or cESRTypeOr
 * @return number of ESR groups
 */
public int renumberESRGroups(int type) {
	return oclMolecule.renumberESRGroups(type);
}

/**
 * High level function for constructing a molecule.
 * After the deletion the original order of atom and bond indexes is retained.
 * @param atom
 */
public void deleteAtom(int atom) {
	oclMolecule.deleteAtom(atom);
}

/**
 * High level function for constructing a molecule.
 * @param x
 * @param y
 * @return
 */
public boolean deleteAtomOrBond(double x, double y) {
	return oclMolecule.deleteAtomOrBond(x, y);
}

/**
 * High level function for constructing a molecule.
 * After the deletion the original order of atom and bond indexes is retained.
 * @param bond
 */
public void deleteBond(int bond) {
	oclMolecule.deleteBond(bond);
}

/**
 * High level function for constructing a molecule.
 * After the deletion the original order of atom and bond indexes is retained.
 * @param bond
 */
public void deleteBondAndSurrounding(int bond) {
	oclMolecule.deleteBondAndSurrounding(bond);
}
	
/**
 * High level function for constructing a molecule.
 * After the deletion the original order of atom and bond indexes is retained.
 * @param atomList
 * @return
 */
public int[] deleteAtoms(int[] atomList) {
	return oclMolecule.deleteAtoms(atomList);
}

/**
 * High level function for constructing a molecule.
 * Delete all selected atoms and all bonds attached to them.
 * After the deletion the original order of atom and bond indexes is retained.
 * @return
 */
public boolean deleteSelectedAtoms() {
	return oclMolecule.deleteSelectedAtoms();
}

/**
 * Marks this atom to be deleted in a later call to deleteMarkedAtomsAndBonds().
 * @param atom
 */
public void markAtomForDeletion(int atom) {
	oclMolecule.markAtomForDeletion(atom);
}

/**
 * Marks this bond to be deleted in a later call to deleteMarkedAtomsAndBonds().
 * @param bond
 */
public void markBondForDeletion(int bond) {
	oclMolecule.markBondForDeletion(bond);
}

/**
 * Checks whether this atom was marked to be deleted and not deleted yet.
 * @param atom
 * @return
 */
public boolean isAtomMarkedForDeletion(int atom) {
	return oclMolecule.isAtomMarkedForDeletion(atom);
}

/**
 * Checks whether this bond was marked to be deleted and not deleted yet.
 * @param bond
 * @return
 */
public boolean isBondMarkedForDeletion(int bond) {
	return oclMolecule.isBondMarkedForDeletion(bond);
}

/**
 * High level function for constructing a molecule.
 * Deletes all atoms and bonds from the molecule, which were marked before for deletion
 * by calling markAtomForDeletion() or markBondForDeletion(). Bonds connecting atoms
 * of which at least one is marked for deletion, are deleted automatically and don't
 * require to be explicitly marked.<br>
 * When multiple atoms and/or bonds need to be deleted, marking them and calling
 * this method is more efficient than deleting them individually with deleteAtom() and
 * deleteBond().
 * Bonds, whose atoms carry opposite charges are treated in the following manner: If only one of
 * the two bond atoms is kept, then its absolute charge will be reduced by 1.
 * After the deletion the original order of atom and bond indexes is retained.
 * @return mapping from old to new atom indices; null if no atoms nor bonds were deleted
 */
public int[] deleteMarkedAtomsAndBonds() {
	return oclMolecule.deleteMarkedAtomsAndBonds();
}

/**
 * Empties the molecule to serve as container for constructing a new molecule,
 * e.g. by multiply calling addAtom(...), addBond(...) and other high level methods.
 */
public void deleteMolecule() {
		oclMolecule.deleteMolecule();
}


public void removeAtomSelection() {
	oclMolecule.removeAtomSelection();
}


public void removeAtomColors() {
	oclMolecule.removeAtomColors();
}

/**
 * This removes all custom labels from the atoms.
 */
public void removeAtomCustomLabels() {
	oclMolecule.removeAtomCustomLabels();
}


public void removeAtomMarkers() {
	oclMolecule.removeAtomMarkers();
}


public void removeBondHiliting() {
	oclMolecule.removeBondHiliting();
}

/**
 * @param pickx
 * @param picky
 * @return index of closest of nearby atoms or -1, if no atom is close
 */
public int findAtom(double pickx, double picky) {
	return oclMolecule.findAtom(pickx, picky);
}

/**
 * @param pickx
 * @param picky
 * @return index of closest of nearby bonds or -1, if no bond is close
 */
public int findBond(double pickx, double picky) {
	return oclMolecule.findBond(pickx, picky);
}

/**
 * @return the number of all atoms, which includes hydrogen atoms.
 */
public int getAllAtoms() {
	return oclMolecule.getAllAtoms();
}

/**
 * @return the number of all bonds, which includes those connecting hydrogen atoms.
 */
public int getAllBonds() {
	return oclMolecule.getAllBonds();
}

/**
 * Get an atom's defined maximum valance if different from the default one.
 * @param atom
 * @return valence 0-14: new maximum valence; -1: use default
 */
public int getAtomAbnormalValence(int atom) {
	return oclMolecule.getAtomAbnormalValence(atom);
}

/**
 * @param atom
 * @return the formal atom charge
 */
public int getAtomCharge(int atom) {
	return oclMolecule.getAtomCharge(atom);
}

/**
 * The atom Cahn-Ingold-Prelog parity is a calculated property available above/equal helper level cHelperCIP.
 * It encodes the stereo configuration of an atom with its neighbors using up/down-bonds
 * or 3D-atom-coordinates, whatever is available. It depends on the atom indices of the neighbor
 * atoms and their orientation is space. This method is called by the Canonizer and usually should not
 * be called otherwise.
 * @param atom
 * @return one of cAtomCIPParityNone,cAtomCIPParityRorM,cAtomCIPParitySorP,cAtomCIPParityProblem
 */
public int getAtomCIPParity(int atom) {
	return oclMolecule.getAtomCIPParity(atom);
}


public int getAtomColor(int atom) {
	return oclMolecule.getAtomColor(atom);
}

/**
 * This is MDL's enhanced stereo representation (ESR).
 * Stereo atoms and bonds with the same ESR type (AND or OR) and the same ESR group number
 * are in the same group, i.e. within this group they have the defined (relative) stereo configuration.
 * @param atom
 * @return group index starting with 0
 */
public int getAtomESRGroup(int atom) {
	return oclMolecule.getAtomESRGroup(atom);
}

/**
 * This is MDL's enhanced stereo representation (ESR).
 * Stereo atoms and bonds with the same ESR type (AND or OR) and the same ESR group number
 * are in the same group, i.e. within this group they have the defined (relative) stereo configuration.
 * @param atom
 * @return one of cESRTypeAbs,cESRTypeAnd,cESRTypeOr
 */
public int getAtomESRType(int atom) {
	return oclMolecule.getAtomESRType(atom);
}

/**
 * In addition to the natural atomic numbers, we support additional pseudo atomic numbers.
 * Most of these are for compatibility with the MDL atom table, e.g. for amino acids and R-groups.
 * D and T are accepted for setting, but are on-the-fly translated to H with the proper atom mass.
 * @param atom
 * @return
 */
public int getAtomicNo(int atom) {
	return oclMolecule.getAtomicNo(atom);
}

/**
 * If a custom atom label is set, a molecule depiction displays
 * the custom label instead of the original one.
 * @param atom
 * @return null or previously defined atom custom label
 */
public String getAtomCustomLabel(int atom) {
	return oclMolecule.getAtomCustomLabel(atom);
}

/**
 * @param atom
 * @return standard atom label of the atom: C,Li,Sc,...
 */
public String getAtomLabel(int atom) {
	return oclMolecule.getAtomLabel(atom);
}

/**
 * The list of atoms that are allowed at this position during sub-structure search.
 * (or refused atoms, if atom query feature cAtomQFAny is set).
 * @param atom
 * @return null or sorted list of unique atomic numbers, if defined
 */
public int[] getAtomList(int atom) {
	return oclMolecule.getAtomList(atom);
}


public String getAtomListString(int atom) {
		return oclMolecule.getAtomListString(atom);
}

/**
 * Returns an atom mapping number within the context of a reaction.
 * Atoms that that share the same mapping number on the reactant and product side
 * are considered to be the same atom.
 * @param atom
 * @return
 */
public int getAtomMapNo(int atom) {
	return oclMolecule.getAtomMapNo(atom);
}

/**
 * @param atom
 * @return atom mass, if is specific isotop, otherwise 0 for natural abundance
 */
public int getAtomMass(int atom) {
	return oclMolecule.getAtomMass(atom);
}

/**
 * The atom parity is a calculated property available above/equal helper level cHelperParities.
 * It describes the stereo configuration of a chiral atom and is calculated either from
 * 2D-atom-coordinates and up/down-bonds or from 3D-atom-coordinates, whatever is available.
 * It depends on the atom indexes of the neighbor atoms and their orientation in space.<br>
 * The parity is defined as follows: Look at the chiral atom such that its neighbor atom with the
 * highest atom index (or the hydrogen atom if it is implicit) is oriented to the back.
 * If the remaining three neighbors are in clockwise order (considering ascending atom indexes)
 * than the parity is 1. If they are in anti-clockwise order, then the parity is 2.<br>
 * For linear chirality (allenes): Look along the straight line of double bonds such that the
 * rear neighbor with the lower atom index points to the top. If the front neighbor with the
 * lower atom index points to the right than the parity is 1.<br>
 * @param atom
 * @return one of cAtomParity1,cAtomParity2,cAtomParityNone,cAtomParityUnknown
 */
public int getAtomParity(int atom) {
	return oclMolecule.getAtomParity(atom);
}

/**
 * Returns all set query features for this atom. In order to get all features related to a certain subject
 * use something like this: <i>getAtomQueryFeatures() & cAtomQFHydrogen</i>
 * @param atom
 * @return
 */
public int getAtomQueryFeatures(int atom) {
	return oclMolecule.getAtomQueryFeatures(atom);
}

/**
 * Gets an atom's radical state as singulet,dublet,triplet or none
 * @param atom
 * @return one of cAtomRadicalStateNone,cAtomRadicalStateS,cAtomRadicalStateD,cAtomRadicalStateT
 */
public int getAtomRadical(int atom) {
	return oclMolecule.getAtomRadical(atom);
}


public double getAtomX(int atom) {
	return oclMolecule.getAtomX(atom);
}


public double getAtomY(int atom) {
	return oclMolecule.getAtomY(atom);
}


public double getAtomZ(int atom) {
	return oclMolecule.getAtomZ(atom);
}


public static double getDefaultAverageBondLength() {
	return StereoMolecule.getDefaultAverageBondLength();
}

/**
 * When the molecule adds a new bond to a new atom or a new ring,
 * then atoms are positioned such that the lengths of the new bonds
 * are equal to the average length of existing bonds. If there are no
 * existing bonds, then this default is used.
 * If the default is not set by this function, then it is 24.
 * @param defaultAVBL
 */
public static void setDefaultAverageBondLength(double defaultAVBL) {
	StereoMolecule.setDefaultAverageBondLength(defaultAVBL);
}
	
public double getBondAngle(int atom1, int atom2) {
	return oclMolecule.getBondAngle(atom1, atom2);
}

/**
 * Calculates a signed torsion as an exterior spherical angle
 * from a valid 4-atom strand.
 * Looking along the central bond, the torsion angle is 0.0, if the
 * projection of front and rear bonds point in the same direction.
 * If the front bond is rotated in the clockwise direction, the angle
 * increases, i.e. has a positive value.
 * http://en.wikipedia.org/wiki/Dihedral_angle
 * @param atom 4 valid atom indices defining a connected atom sequence
 * @return torsion in the range: -pi <= torsion <= pi
 */
public double calculateTorsion(int[] atom) {
	return oclMolecule.calculateTorsion(atom);
}

/**
 * @param no 0 or 1
 * @param bond
 * @return atom index
 */
public int getBondAtom(int no,int bond) {
	return oclMolecule.getBondAtom(no,bond);
}

/**
 * The bond Cahn-Ingold-Prelog parity is a calculated property available above/equal helper level cHelperCIP.
 * It encodes the stereo configuration of a bond with its neighbors using 2D-coordinates and up/down-bonds
 * or 3D-atom-coordinates, whatever is available. It depends on the atom indices of the neighbor
 * atoms and their orientation is space. This method is called by the Canonizer and usually should not
 * be called otherwise. Considered are E/Z-double bonds and M/P-BINAP type single bonds.
 * @param bond
 * @return one of cBondCIPParityNone,cBondCIPParityEorP,cBondCIPParityZorM,cBondCIPParityProblem
 */
public int getBondCIPParity(int bond) {
	return oclMolecule.getBondCIPParity(bond);
}

/**
 * This is MDL's enhanced stereo representation (ESR).
 * Stereo atoms and bonds with the same ESR type (AND or OR) and the same ESR group number
 * are in the same group, i.e. within this group they have the defined (relative) stereo configuration.
 * @param bond
 * @return group index starting with 0
 */
public int getBondESRGroup(int bond) {
	return oclMolecule.getBondESRGroup(bond);
}

/**
 * This is MDL's enhanced stereo representation (ESR).
 * Stereo atoms and bonds with the same ESR type (AND or OR) and the same ESR group number
 * are in the same group, i.e. within this group they have the defined (relative) stereo configuration.
 * @param bond
 * @return one of cESRTypeAbs,cESRTypeAnd,cESRTypeOr
 */
public int getBondESRType(int bond) {
	return oclMolecule.getBondESRType(bond);
}

/**
 * @param bond
 * @return bond length calculated from atom 2D-coordinates.
 */
public double getBondLength(int bond) {
	return oclMolecule.getBondLength(bond);
}

/**
 * Delocalized bonds, i.e. bonds in an aromatic 6-membered ring, are returned as 1.
 * Ligand field bonds are returned as 0.
 * @param bond
 * @return for organic molecules 1,2, or 3
 */
public int getBondOrder(int bond) {
	return oclMolecule.getBondOrder(bond);
}

/**
 * Returns the pre-calculated bond parity, e.g. cBondParityEor1.
 * To distinguish double bond parities (E/Z) from parities of axial
 * chirality, e.g. BINAP type (1/2) simply check with getBondOrder(bond):
 * If the order is 2, then the parity describes E/Z, otherwise an axial parity.
 * @param bnd
 * @return one of cBondParity???
 */
public int getBondParity(int bnd) {
	return oclMolecule.getBondParity(bnd);
}


public int getBondQueryFeatures(int bnd) {
	return oclMolecule.getBondQueryFeatures(bnd);
}


public boolean isBondBridge(int bond) {
	return oclMolecule.isBondBridge(bond);
}


public int getBondBridgeMinSize(int bond) {
	return oclMolecule.getBondBridgeMinSize(bond);
}


public int getBondBridgeMaxSize(int bond) {
	return oclMolecule.getBondBridgeMaxSize(bond);
}

/**
 * Returns bond type combining bond order and stereo orientation.
 * @param bond
 * @return one of cBondTypeSingle,cBondTypeDouble,cBondTypeUp,cBondTypeCross,...
 */
public int getBondType(int bond) {
	return oclMolecule.getBondType(bond);
}

/**
 * This is the bond type without stereo information.
 * @param bond
 * @return cBondTypeSingle,cBondTypeDouble,cBondTypeTriple,cBondTypeDelocalized
 */
public int getBondTypeSimple(int bond) {
	return oclMolecule.getBondTypeSimple(bond);
}

/**
 * Gets the overall chirality of the molecule, which is a calculated information considering:
 * Recognition of stereo centers and stereo bonds, defined ESR features, meso detection.
 * The chirality combines the knowledge about how many stereo isomers are represented,
 * whether all of these are meso, whether we have one defined stereo isomer, a mixture
 * of racemates, epimers, or other diastereomers.
 * The information is used during depiction.
 */
public int getChirality() {
	return oclMolecule.getChirality();
}

/**
 * The currently defined maximum of atoms, which increases automatically when using high level
 * construction methods and new atoms exceed the current maximum.
 * @return
 */
public int getMaxAtoms() {
	return oclMolecule.getMaxAtoms();
}

/**
 * Usually called automatically and hardly needed to be called.
 * @param v
 */
public void setMaxAtoms(int v) {
	oclMolecule.setMaxAtoms(v);
}

/**
 * The currently defined maximum of bonds, which increases automatically when using high level
 * construction methods and new bonds exceed the current maximum.
 * @return
 */
public int getMaxBonds() {
	return oclMolecule.getMaxBonds();
}

/**
 * Usually called automatically and hardly needed to be called.
 * @param v
 */
public void setMaxBonds(int v) {
	oclMolecule.setMaxBonds(v);
}

	/**
	 * cMoleculeColorDefault: atom coloring depends on atomic number. Carbon and hydrogen are drawn in neutral color<br>
	 * cMoleculeColorNeutral: all atoms and bonds and CIP letters are drawn in neutral color<br>
	 * @return cMoleculeColorNeutral or cMoleculeColorDefault. In future may also return ARGB values.
	 */
	public int getMoleculeColor() {
		return oclMolecule.getMoleculeColor();
	}
	
	/**
	 * Currently, this method only allows to switch the default atomic number dependent atom coloring off
	 * by passing cMoleculeColorNeutral. In future updates it may also accept ARGB values.
	 * @param color currently supported values: cMoleculeColorDefault, cMoleculeColorNeutral
	 */
	public void setMoleculeColor(int color) {
		oclMolecule.setMoleculeColor(color);
	}
	
/**
 * Allows to set a molecule name or identifier, that is, for instance, written to or read from molfiles.
 * @return
 */
public String getName() {
	return oclMolecule.getName();
}

/**
 * The stereo problem flag is set by the stereo recognition (available equal/above helper level cHelperParities)
 * if an atom has over- or under-specified stereo bonds attached, i.e. a stereo center with less or more than one
 * up/down-bond, an non-stereo-center atom carrying (a) stereo bond(s), or a stereo center with neighbors coordinates
 * such that the stereo configuration cannot be deduced. This flag is used by the depiction and causes affected atoms
 * to be drawn in margenta.
 * @param atom
 * @return
 */
public boolean getStereoProblem(int atom) {
	return oclMolecule.getStereoProblem(atom);
}

/**
 * @param atom
 * @return whether the atom's stereo configuration was explicitly declared unknown
 */
public boolean isAtomConfigurationUnknown(int atom) {
	return oclMolecule.isAtomConfigurationUnknown(atom);
}

/**
 * Pseudo paries are parities that indicate a relative configuration.
 * It always needs at least 2 pseudo parities (atom or bond) within
 * a part of a molecule to be meaningful.
 * This information is calculated by ensureHelperArrays(Molecule.cHelperCIP).
 * Molecules extracted from IDCode don't know about pseudo parities.
 * @param atom
 * @return wether this atom's parity is a relative configuration
 */
public boolean isAtomParityPseudo(int atom) {
	return oclMolecule.isAtomParityPseudo(atom);
}

/**
 * Atoms with pseudo parities are not considered stereo centers.
 * While parities are canonized and always refer to the full set
 * of molecules (in case ESR groups are defined), this method
 * returns true if this atom is a stereo center in any(!) of the
 * individual molecules described by the ESR settings.
 * @param atom
 * @return true if atom is stereo center in at least one molecule after ESR resolution
 */
public boolean isAtomStereoCenter(int atom) {
	return oclMolecule.isAtomStereoCenter(atom);
}


public boolean isBondParityPseudo(int bond) {
	return oclMolecule.isBondParityPseudo(bond);
}

/**
 * This hint/flag is set by CoordinateInventor for double bonds without given EZ-parity,
 * because the new coordinates may imply a not intended EZ-parity. If parities are calculated
 * later by the Canonizer is can correctly assign cBondParityUnknown if the bond is a stereo bond.
 * The setBondParity() method clears this flag.
 * This method usually should not be called for other purposes.
 * @return whether the bond parity was unknown when 2D- atom coordinates were created
 */
public boolean isBondParityUnknownOrNone(int bond) {
	return oclMolecule.isBondParityUnknownOrNone(bond);
}

/**
 * Molecule objects may represent complete molecules or sub-structure fragments,
 * depending on, whether they are flagges as being a fragment or not. Both representations
 * have much in common, but in certain aspects behave differently. Thus, complete molecules
 * are considered to carry implicit hydrogens to fill unoccupied atom valences.
 * Sub-structure fragments on the other hand may carry atom or bond query features.
 * Depiction, sub-structure search, and other algorithms treat fragments and complete molecules differerently.
 * @return
 */
public boolean isFragment() {
	return oclMolecule.isFragment();
}

/**
 * @param atom
 * @return whether the atom has the natural isotop distribution
 */
public boolean isNaturalAbundance(int atom) {
	return oclMolecule.isNaturalAbundance(atom);
}

/**
 * @return true if atom is one of H,B,C,N,O,F,Si,P,S,Cl,As,Se,Br,Te,I
 */
public boolean isPurelyOrganic() {
	return oclMolecule.isPurelyOrganic();
}


public boolean isSelectedAtom(int atom) {
	return oclMolecule.isSelectedAtom(atom);
}

/**
 * Atom marking may be used for any external purpose
 */
public boolean isMarkedAtom(int atom) {
	return oclMolecule.isMarkedAtom(atom);
}

/**
 * Used for depiction only.
 * @param bond
 */
public boolean isBondBackgroundHilited(int bond) {
	return oclMolecule.isBondBackgroundHilited(bond);
}

/**
 * Used for depiction only.
 * @param bond
 */
public boolean isBondForegroundHilited(int bond) {
	return oclMolecule.isBondForegroundHilited(bond);
}


public boolean isSelectedBond(int bond) {
	return oclMolecule.isSelectedBond(bond);
}


public boolean isAutoMappedAtom(int atom) {
	return oclMolecule.isAutoMappedAtom(atom);
}

/**
 * Checks whether bond is drawn as up/down single bond
 * @param bond
 * @return true if bond is a stereo bond
 */
public boolean isStereoBond(int bond) {
	return oclMolecule.isStereoBond(bond);
}

/**
 * Low level method for constructing/modifying a molecule from scratch.
 * Use setAtomicNo(), possibly setAtomX(), setAtomY() and other setAtomXXX() methods for new atoms.
 * @param no
 */
public void setAllAtoms(int no) {
	oclMolecule.setAllAtoms(no);
}

/**
 * Low level method for constructing/modifying a molecule from scratch.
 * Use setBondType() and setBondAtom() if you increase the number of bonds with this method.
 * @param no
 */
public void setAllBonds(int no) {
	oclMolecule.setAllBonds(no);
}

/**
 * Set an atom's maximum valance to be different from the default one.
 * If a carbon atom's valence is set to -1,0 or 4 its radical state is removed.
 * If a carbon atom's valence is set to 2, a singulet carbene state is assumed.
 * @param atom
 * @param valence 0-14: new maximum valence; -1: use default
 */
public void setAtomAbnormalValence(int atom, int valence) {
	oclMolecule.setAtomAbnormalValence(atom, valence);
}


public void setAtomCharge(int atom, int charge) {
	oclMolecule.setAtomCharge(atom, charge);
}


public void setAtomColor(int atom,int color) {
	oclMolecule.setAtomColor(atom,color);
}

/**
 * This is a user applied information, rather than a calculated value.
 * The stereo center configuration is declared to be unknown.
 * If the atom is recognized a stereo center, then its parity will be cAtomParityUnknown.
 * @param atom
 * @param u
 */
public void setAtomConfigurationUnknown(int atom, boolean u) {
	oclMolecule.setAtomConfigurationUnknown(atom, u);
}


public void setAtomSelection(int atom,boolean s) {
	oclMolecule.setAtomSelection(atom,s);
}

/**
 * Atom marking may be used for any external purpose
 */
public void setAtomMarker(int atom,boolean s) {
	oclMolecule.setAtomMarker(atom,s);
}

/**
 * Set an atom's atomic number and defines the isotop to be natural abundance.
 * @param atom
 * @param no
 */
public void setAtomicNo(int atom,int no) {
	oclMolecule.setAtomicNo(atom,no);
}

/**
 * Defines an atom list as query feature for substructure search
 * @param atom
 * @param list is null or a sorted int[] of valid atomic numbers
 * @param isExcludeList true if atom is a wild card and list contains atoms to be excluded
 */
public void setAtomList(int atom, int[] list, boolean isExcludeList) {
	oclMolecule.setAtomList(atom, list, isExcludeList);
}

/**
 * Defines an atom mapping number within the context of a reaction.
 * Atoms that that share the same mapping number on the reactant and product side
 * are considered to be the same atom.
 * @param atom
 * @param mapNo
 * @param autoMapped
 */
public void setAtomMapNo(int atom, int mapNo, boolean autoMapped) {
	oclMolecule.setAtomMapNo(atom, mapNo, autoMapped);
}

/**
 * Set atom to specific isotop or to have a natural isotop distribution
 * @param atom
 * @param mass rounded atom mass or 0 (default) for natural abundance
 */
public void setAtomMass(int atom, int mass) {
	oclMolecule.setAtomMass(atom, mass);
}

/**
 * The atom parity is a calculated property available above/equal helper level cHelperParities.
 * It describes the stereo configuration of a chiral atom and is calculated either from
 * 2D-atom-coordinates and up/down-bonds or from 3D-atom-coordinates, whatever is available.
 * It depends on the atom indices of the neighbor atoms and their orientation in space.<br>
 * The parity is defined as follows: Look at the chiral atom such that its neighbor atom with the
 * highest atom index (or the hydrogen atom if it is implicit) is oriented to the back.
 * If the remaining three neighbors are in clockwise order (considering ascending atom indexes)
 * than the parity is 1. If they are in anti-clockwise order, then the parity is 2.<br>
 * For linear chirality (allenes): Look along the straight line of double bonds such that the
 * rear neighbor with the lower atom index points to the top. If the front neighbor with the
 * lower atom index points to the right than the parity is 1.<br>
 * This method is called by the Canonizer and usually should not be called otherwise.
 * @param atom
 * @param parity one of cAtomParity1,cAtomParity2,cAtomParityNone,cAtomParityUnknown
 * @param isPseudo true if the configuration is only meaningful relative to another one
 */
public void setAtomParity(int atom, int parity, boolean isPseudo) {
	oclMolecule.setAtomParity(atom, parity, isPseudo);
}

/**
 * Introduce or remove an atom query feature and make sure, the molecule is flagged
 * to be a sub-structure fragment (see setFragment()).
 * A query feature is usually a flag, which if set, poses an additional atom/bond matching constraint
 * for the sub-structure search and, thus, reduces the number of matching atoms and therefore also
 * the number of molecules found. Often multiple query feature flags are related and grouped, e.g.
 * to define the number of hydrogens atoms. These are the flags related to hydrogen neighbors:<br><br>
 * public static final int cAtomQFHydrogen		= 0x00000780;<br>
 * public static final int cAtomQFNot0Hydrogen	= 0x00000080;<br>
 * public static final int cAtomQFNot1Hydrogen	= 0x00000100;<br>
 * public static final int cAtomQFNot2Hydrogen	= 0x00000200;<br>
 * public static final int cAtomQFNot3Hydrogen	= 0x00000400;<br>
 * <p>An inverse logic needs to be applied to translate a user request to the bits needed. For example,
 * to only accept atoms that have 1 or 2 hydrogen neighbors, we need to filter out all others. Thus, we
 * would call<br>setAtomQueryFeature(atom, cAtomQFNot0Hydrogen | cAtomQFNot3Hydrogen, true);</p>
 * <p>To match only atoms without hydrogen neighbors, call<br>setAtomQueryFeature(atom, cAtomQFHydrogen & ~cAtomQFNot3Hydrogen, true);<br>
 * This mechanism allows a very efficient atom matching and therefore very fast sub-structure search.</p>
 * @param atom
 * @param feature one of cAtomQF...
 * @param value if true, the feature is set, otherwise it is removed
 */
public void setAtomQueryFeature(int atom, int feature, boolean value) {
	oclMolecule.setAtomQueryFeature(atom, feature, value);
}

/**
 * Sets an atom's radical state as singulet,dublet,triplet or none
 * @param atom
 * @param radical one of cAtomRadicalStateNone,cAtomRadicalStateS,cAtomRadicalStateD,cAtomRadicalStateT
 */
public void setAtomRadical(int atom, int radical) {
	oclMolecule.setAtomRadical(atom, radical);
}

/**
 * The atom Cahn-Ingold-Prelog parity is a calculated property available above/equal helper level cHelperCIP.
 * It encodes the stereo configuration of an atom with its neighbors using up/down-bonds
 * or 3D-atom-coordinates, whatever is available. It depends on the atom indices of the neighbor
 * atoms and their orientation is space. This method is called by the Canonizer and usually should not
 * be called otherwise.
 * @param atom
 * @param parity one of cAtomCIPParityRorM,cAtomCIPParitySorP,cAtomCIPParityProblem
 */
public void setAtomCIPParity(int atom, int parity) {
	oclMolecule.setAtomCIPParity(atom, parity);
}


public void setAtomX(int atom, double x) {
	oclMolecule.setAtomX(atom, x);
}


public void setAtomY(int atom, double y) {
	oclMolecule.setAtomY(atom, y);
}


public void setAtomZ(int atom, double z) {
	oclMolecule.setAtomZ(atom, z);
}


public void setBondAtom(int no, int bond, int atom) {
	oclMolecule.setBondAtom(no, bond, atom);
}

/**
 * The bond Cahn-Ingold-Prelog parity is a calculated property available above/equal helper level cHelperCIP.
 * It encodes the stereo configuration of a bond with its neighbors using 2D-coordinates and up/down-bonds
 * or 3D-atom-coordinates, whatever is available. It depends on the atom indices of the neighbor
 * atoms and their orientation is space. This method is called by the Canonizer and usually should not
 * be called otherwise. Considered are E/Z-double bonds and M/P-BINAP type single bonds.
 * @param bond
 * @param parity one of cBondCIPParityEorP,cBondCIPParityZorM,cBondCIPParityProblem
 */
public void setBondCIPParity(int bond, int parity) {
	oclMolecule.setBondCIPParity(bond, parity);
}

/**
 * Used for depiction only.
 * @param bond
 * @param s
 */
public void setBondBackgroundHiliting(int bond, boolean s) {
	oclMolecule.setBondBackgroundHiliting(bond, s);
}

/**
 * Used for depiction only.
 * @param bond
 * @param s
 */
public void setBondForegroundHiliting(int bond, boolean s) {
	oclMolecule.setBondForegroundHiliting(bond, s);
}

/**
 * The bond parity is a calculated property available above/equal helper level cHelperParities.
 * It encodes the stereo configuration of a double bond or BINAP type single bond from up/down-bonds
 * and 2D-coordinates or 3D-atom-coordinates, whatever is available. It depends on the atom indices
 * of the neighbor atoms and their orientation is space. This method is called by the Canonizer and
 * usually should not be called otherwise.
 * @param bond
 * @param parity one of cBondParityEor1,cBondParityZor2,cBondParityNone,cBondParityUnknown
 * @param isPseudo true if the configuration is only meaningful relative to another one
 */
public void setBondParity(int bond, int parity, boolean isPseudo) {
	oclMolecule.setBondParity(bond, parity, isPseudo);
}

/**
 * This hint/flag is set by CoordinateInventor for double bonds without given EZ-parity,
 * because the new coordinates may imply a not intended EZ-parity. If parities are calculated
 * later by the Canonizer is can correctly assign cBondParityUnknown if the bond is a stereo bond.
 * The setBondParity() method clears this flag.
 * This method usually should not be called for other purposes.
 * @param bond
 */
public void setBondParityUnknownOrNone(int bond) {
	oclMolecule.setBondParityUnknownOrNone(bond);
}


public void setBondQueryFeature(int bond, int feature, boolean value) {
	oclMolecule.setBondQueryFeature(bond, feature, value);
}

/**
 * Sets the bond type based on bond order without stereo orientation.
 * @param bond
 * @param order 1,2, or 3
 */
public void setBondOrder(int bond,int order) {
	oclMolecule.setBondOrder(bond,order);
}

/**
 * Defines a bond type combining bod order and stereo orientation.
 * @param bond
 * @param type one of cBondTypeSingle,cBondTypeDouble,cBondTypeUp,cBondTypeCross,...
 */
public void setBondType(int bond,int type) {
	oclMolecule.setBondType(bond,type);
}

/**
 * Sets the overall chirality of the molecule taking into account:
 * Recognition of stereo centers and stereo bonds, defined ESR features, meso detection.
 * The chirality combines the knowledge about how many stereo isomers are represented,
 * whether all of these are meso, whether we have one defined stereo isomer, a mixture
 * of racemates, epimers, or other diastereomers.
 * The information is used during depiction.
 * This method is called by the Canonizer and usually should not be called otherwise.
 * @param c
 */
public void setChirality(int c) {
	oclMolecule.setChirality(c);
}

/**
 * Fragment's query features are checked for consistency and normalized
 * during helper array creation. As part of this, simple hydrogen atoms
 * are converted into hydrogen-count query features. If hydrogen protection
 * is enabled, explicit hydrogens are not touched.
 * @param protectHydrogen
 */
public void setHydrogenProtection(boolean protectHydrogen) {
	oclMolecule.setHydrogenProtection(protectHydrogen);
}

	/**
	 * Use this method with extreme care. If you make a change to the molecule,
	 * the validity of the helper arrays is typically set to cHelperNone.
	 * If you make a small change to a molecule that doesn't change its topology,
	 * you may override the automatic automatically cleared helper validity with
	 * this method and avoid a new calculation of the neighbour arrays and ring
	 * detection.
	 * @param helperValidity cHelperNeighbours or cHelperRings
	 */
	public void setHelperValidity(int helperValidity) {
		oclMolecule.setHelperValidity(helperValidity);
	}
	
/**
 * This is for compatibility with old MDL stereo representation
 * that contained a 'chiral' flag to indicate that the molecule
 * is not a racemate. If a molecule is constructed from a source
 * format (e.g. a molfile version 2) that contains a 'chiral' flag
 * then setToRacemate() needs to be called if the chiral flag is
 * not(!) set. This causes after stereo center recognition to
 * turn all absolute stereo centers into racemic ones.
 */
public void setToRacemate() {
	oclMolecule.setToRacemate();
}

/**
 * If a custom atom label is set, a molecule depiction displays
 * the custom label instead of the original one. Custom labels
 * are not interpreted otherwise. However, they may optionally
 * be encoded into idcodes; see Canonizer.encodeAtomCustomLabels().
	 * If a custom label start with ']' then the label without the ']'
	 * symbol is shown at the top left of the original atom label rather than
	 * replacing the original atom label.
	 * If label is null or equals the normal atom label, then the custom label
 * is removed. This method is less efficient than the byte[] version:
 * setAtomCustomLabel(int, byte[])
 * @param atom
 * @param label null to remove custom label
 */
public void setAtomCustomLabel(int atom, String label) {
	oclMolecule.setAtomCustomLabel(atom, label);
}

/**
 * This is MDL's enhanced stereo representation (ESR).
 * Stereo atoms and bonds with the same ESR type (AND or OR) and the same ESR group number
 * are in the same group, i.e. within this group they have the defined (relative) stereo configuration.
 * @param atom
 * @param type one of cESRTypeAbs,cESRTypeAnd,cESRTypeOr
	 * @param group index starting with 0 (not considered if type is cESRTypeAbs)
 */
public void setAtomESR(int atom, int type, int group) {
	oclMolecule.setAtomESR(atom, type, group);
}

/**
 * MDL's enhanced stereo representation for BINAP type of stereo bonds.
 * Stereo atoms and bonds with the same ESR type (AND or OR) and the same ESR group number
 * are in the same group, i.e. within this group they have the defined (relative) stereo configuration.
 * @param bond
 * @param type one of cESRTypeAbs,cESRTypeAnd,cESRTypeOr
 * @param group index starting with 0
 */
public void setBondESR(int bond, int type, int group) {
	oclMolecule.setBondESR(bond, type, group);
}

/**
 * Molecule objects may represent complete molecules or sub-structure fragments,
 * depending on, whether they are flagges as being a fragment or not. Both representations
 * have much in common, but in certain aspects behave differently. Thus, complete molecules
 * are considered to carry implicit hydrogens to fill unoccupied atom valences.
 * Sub-structure fragments on the other hand may carry atom or bond query features.
 * Depiction, sub-structure search, and other algorithms treat fragments and complete molecules
 * differently.
 * @param isFragment if false, then all query features are removed
 */
public void setFragment(boolean isFragment) {
	oclMolecule.setFragment(isFragment);
}


public void setName(String name) {
	oclMolecule.setName(name);
}

	/**
	 * Removes any query features from the molecule
	 * @return whether any query features were removed
	 */
	public boolean removeQueryFeatures() {
		return oclMolecule.removeQueryFeatures();
	}
	
/**
 * Removes all isotop information, i.e. sets all atoms to the natural isotop abundance.
 * @return true if something was changed
 */
public boolean stripIsotopInfo() {
	return oclMolecule.stripIsotopInfo();
}


public void translateCoords(double dx, double dy) {
	oclMolecule.translateCoords(dx, dy);
}


public void scaleCoords(double f) {
	oclMolecule.scaleCoords(f);
}


public void zoomAndRotateInit(double x, double y) {
	oclMolecule.zoomAndRotateInit(x, y);
}


public void zoomAndRotate(double zoom, double angle, boolean selected) {
	oclMolecule.zoomAndRotate(zoom, angle, selected);
}

/**
 * This is the defined maximum valence (or set abnormal valence)
 * neglecting atom charge or radical influences, e.g. N or N(+) -> 3.
 * @param atom
 * @return
 */
public int getMaxValenceUncharged(int atom) {
	return oclMolecule.getMaxValenceUncharged(atom);
}

/**
 * This is the default maximum valence of the atom
 * neglecting atom charge or radical influences, e.g. N or N(+) -> 3.
 * If the atomic no has multiple valid max valences, it is the highest one.
 * @param atom
 * @return
 */
public int getDefaultMaxValenceUncharged(int atom) {
	return oclMolecule.getDefaultMaxValenceUncharged(atom);
}

/**
 * This is the defined maximum valence (or set abnormal valence)
 * corrected by atom charge or radical influences, e.g. N(+) -> 4.
 * @param atom
 * @return
 */
public int getMaxValence(int atom) {
	return oclMolecule.getMaxValence(atom);
}

/**
 * This is the maximum valence correction caused by atom charge
 * or radical status, e.g. N+ -> 1; N- -> -1; Al+ -> -1; C+,C- -> -1.
 * In some cases, where the atomicNo can have multiple valences,
 * the influence of a charge depends on the atom's actual valence, e.g.
 * valence corrections for R3P(+) and R5P(+) are 1 and -1, respectively.
 * Criteria are:<br>
 * -in the given valence state is there a lone pair that can be protonated<br>
 * -can we introduce a negative substituent as in BH3 or PF5 vs. SF6<br>
 * @param atom
 * @param occupiedValence
 * @return
 */
public int getElectronValenceCorrection(int atom, int occupiedValence) {
	return oclMolecule.getElectronValenceCorrection(atom, occupiedValence);
}


public static boolean isAtomicNoElectronegative(int atomicNo) {
	return StereoMolecule.isAtomicNoElectronegative(atomicNo);
}

/**
 * @param atom
 * @return whether atom is an electronegative one
 */
public boolean isElectronegative(int atom) {
	return oclMolecule.isElectronegative(atom);
}


public static boolean isAtomicNoElectropositive(int atomicNo) {
	return StereoMolecule.isAtomicNoElectropositive(atomicNo);
}

/**
 * @param atom
 * @return whether atom is an electropositive one
 */
public boolean isElectropositive(int atom) {
	return oclMolecule.isElectropositive(atom);
}

/**
 * @param atom
 * @return whether atom is any metal atom
 */
public boolean isMetalAtom(int atom) {
	return oclMolecule.isMetalAtom(atom);
}

/**
 * @param atom
 * @return true if this atom is not a metal and not a nobel gas
 */
public boolean isOrganicAtom(int atom) {
	return oclMolecule.isOrganicAtom(atom);
}

/**
 * Clears destmol and then copies a part of this Molecule into destMol, being defined by a mask of atoms to be included.
 * If not all atoms are copied, then destMol is set to be a substructure fragment.
 * @param destMol receives the part of this Molecule
 * @param includeAtom defines atoms to be copied; its size may be this.getAtoms() or this.getAllAtoms()
 * @param recognizeDelocalizedBonds defines whether disconnected delocalized bonds will keep their
 * single/double bond status or whether the query feature 'delocalized bond' will be set
 * @param atomMap null or int[] not smaller than includeAtom.length; receives atom indices of dest molecule
 */
public void copyMoleculeByAtoms(JSMolecule destMol, boolean[] includeAtom, boolean recognizeDelocalizedBonds, int[] atomMap) {
		oclMolecule.copyMoleculeByAtoms(destMol.getStereoMolecule(), includeAtom, recognizeDelocalizedBonds, atomMap);
}

/**
 * Clears destmol and then copies a part of this Molecule into destMol, being defined by a mask of bonds to be included.
 * Bonds, whose atoms carry opposite charges are treated in the following manner: If only one of
 * the two bond atoms is kept, then its absolute charge will be reduced by 1.
 * @param destMol receives the part of this Molecule
 * @param includeBond defines bonds to be copied
 * @param recognizeDelocalizedBonds defines whether disconnected delocalized bonds will keep their
 * single/double bond status or whether the query feature 'delocalized bond' will be set
 * @param atomMap null or int[] not smaller than this.getAllAtoms()
 * @return atom map from this to destMol with not copied atom's index being -1
 */
public int[] copyMoleculeByBonds(JSMolecule destMol, boolean[] includeBond, boolean recognizeDelocalizedBonds, int[] atomMap) {
		return oclMolecule.copyMoleculeByBonds(destMol.getStereoMolecule(), includeBond, recognizeDelocalizedBonds, atomMap);
}

/**
 * The neighbours (connected atoms) of any atom are sorted by their relevance:<br>
 * 1. non-plain-hydrogen atoms (bond order 1 and above)<br>
 * 2. plain-hydrogen atoms (natural abundance, bond order 1)<br>
 * 3. non-plain-hydrogen atoms (bond order 0, i.e. metall ligand bond)<br>
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @param atom
 * @return count of category 1 & 2 neighbour atoms (excludes neighbours connected with zero bond order)
 */
public int getAllConnAtoms(int atom) {
	return oclMolecule.getAllConnAtoms(atom);
}

/**
 * @param atom
 * @return the number of connected plain explicit and implicit hydrogen atoms
 */
public int getAllHydrogens(int atom) {
	return oclMolecule.getAllHydrogens(atom);
}

/**
 * A validated molecule (after helper array creation) contains a sorted list of all atoms
 * with the plain (neglegible) hydrogen atoms at the end of the list. Neglegible hydrogen atoms
 * a those that can be considered implicit, because they have no attached relevant information.
 * Hydrogen atoms that cannot be neglected are special isotops (mass != 0), if they carry a
 * custom label, if they are connected to another atom with bond order different from 1, or
 * if they are connected to another neglegible hydrogen.<br>
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @return the number relevant atoms not including neglegible hydrogen atoms
 */
public int getAtoms() {
	return oclMolecule.getAtoms();
}

/**
 * @param atom
 * @return count of neighbour atoms connected by a 0-order metal ligand bond
 */
public int getMetalBondedConnAtoms(int atom) {
	return oclMolecule.getMetalBondedConnAtoms(atom);
}

/**
 * This is different from the Hendrickson pi-value, which considers pi-bonds to carbons only.
 * @param atom
 * @return the number pi electrons at atom (the central atom of acetone would have 1)
 */
public int getAtomPi(int atom) {
	return oclMolecule.getAtomPi(atom);
}

/**
 * @param atom
 * @return Hendrickson sigma-value, which is the number attached carbon atoms
 *
public int getAtomSigma(int atom) {
public int getAtomSigma(int atom) {
	return oclMolecule.getAtomSigma(atom);
}

/**
 * @param atom
 * @return Hendrickson Z-value, which is the sum of all bond orders to any attached hetero atoms
 *
public int getAtomZValue(int atom) {
public int getAtomZValue(int atom) {
	return oclMolecule.getAtomZValue(atom);
}

/**
 * @param atom
 * @return 0 or the size of the smallest ring that atom is a member of
 */
public int getAtomRingSize(int atom) {
	return oclMolecule.getAtomRingSize(atom);
}

/**
 * @param bond
 * @return 0 or the size of the smallest ring that bond is a member of
 */
public int getBondRingSize(int bond) {
	return oclMolecule.getBondRingSize(bond);
}

/**
 * The bond list is preprocessed such that all bonds leading to a plain hydrogen atom
 * (natural abundance, no custom labels) are at the end of the list.
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @return count of bonds not including those connecting plain-H atoms
 */
public int getBonds() {
	return oclMolecule.getBonds();
}

/**
 * @return -1 or the bond that connects atom1 with atom2
 */
public int getBond(int atom1, int atom2) {
	return oclMolecule.getBond(atom1, atom2);
}

/**
 * @return a copy of this with all arrays sized to just cover all existing atoms and bonds
 */
public JSMolecule getCompactCopy() {
	return new JSMolecule(oclMolecule.getCompactCopy());
}

/**
 * The neighbours (connected atoms) of any atom are sorted by their relevance:<br>
 * 1. non-plain-hydrogen atoms (bond order 1 and above)<br>
 * 2. plain-hydrogen atoms (natural abundance, bond order 1)<br>
 * 3. non-plain-hydrogen atoms (bond order 0, i.e. metall ligand bond)<br>
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @param atom
 * @param i index into sorted neighbour list
 * @return the i-th neighbor atom of atom
 */
public int getConnAtom(int atom, int i) {
	return oclMolecule.getConnAtom(atom, i);
}

/**
 * The neighbours (connected atoms) of any atom are sorted by their relevance:<br>
 * 1. non-plain-hydrogen atoms (bond order 1 and above)<br>
 * 2. plain-hydrogen atoms (natural abundance, bond order 1)<br>
 * 3. non-plain-hydrogen atoms (bond order 0, i.e. metall ligand bond)<br>
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @param atom
 * @return count of category 1 neighbour atoms (excludes plain H and bond zero orders)
 */
public int getConnAtoms(int atom) {
	return oclMolecule.getConnAtoms(atom);
}

/**
 * The neighbours (connected atoms) of any atom are sorted by their relevance:<br>
 * 1. non-plain-hydrogen atoms (bond order 1 and above)<br>
 * 2. plain-hydrogen atoms (natural abundance, bond order 1)<br>
 * 3. non-plain-hydrogen atoms (bond order 0, i.e. metall ligand bond)<br>
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @param atom
 * @return count of category 1 & 2 & 3 neighbour atoms (excludes neighbours connected with zero bond order)
 */
public int getAllConnAtomsPlusMetalBonds(int atom) {
	return oclMolecule.getAllConnAtomsPlusMetalBonds(atom);
}

/**
 * The neighbours (connected atoms) of any atom are sorted by their relevance:<br>
 * 1. non-plain-hydrogen atoms (bond order 1 and above)<br>
 * 2. plain-hydrogen atoms (natural abundance, bond order 1)<br>
 * 3. non-plain-hydrogen atoms (bond order 0, i.e. metall ligand bond)<br>
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @param atom
 * @param i index into sorted neighbour list
 * @return index of bond connecting atom with its i-th neighbor
 */
public int getConnBond(int atom, int i) {
	return oclMolecule.getConnBond(atom, i);
}

/**
 * The neighbours (connected atoms) of any atom are sorted by their relevance:<br>
 * 1. non-plain-hydrogen atoms (bond order 1 and above)<br>
 * 2. plain-hydrogen atoms (natural abundance, bond order 1)<br>
 * 3. non-plain-hydrogen atoms (bond order 0, i.e. metall ligand bond)<br>
 * Only valid after calling ensureHelperArrays(cHelperNeighbours or higher);
 * @param atom
 * @param i index into sorted neighbour list
 * @return order of bond connecting atom with its i-th neighbor
 */
public int getConnBondOrder(int atom, int i) {
	return oclMolecule.getConnBondOrder(atom, i);
}

/**
 * This method returns the non-hydrogen neighbour count of atom.
 * It excludes any hydrogen atoms in contrast to getConnAtoms(), which only
 * excludes plain hydrogen (not deuterium, tritium, custom labelled hydrogen, etc.).
 * Don't use this method's return value for loops with getConnAtom(),
 * getConnBond(), or getConnBondOrder().
 * @param atom
 * @return the number of non-hydrogen neighbor atoms
 */
public int getNonHydrogenNeighbourCount(int atom) {
	return oclMolecule.getNonHydrogenNeighbourCount(atom);
}

/**
 * Calculates and returns the mean bond length of all bonds including or not
 * including hydrogen bonds.
 * If there are no bonds, then the average distance between unconnected atoms is
 * returned. If we have less than 2 atoms, cDefaultAverageBondLength is returned.
 * @param nonHydrogenBondsOnly
 * @return
 */
public double getAverageBondLength(boolean nonHydrogenBondsOnly) {
	return oclMolecule.getAverageBondLength(nonHydrogenBondsOnly);
}

/**
 * The sum of bond orders of explicitly connected neighbour atoms including explicit hydrogen.
 * The occupied valence includes bonds to atoms with set cAtomQFExcludeGroup flags.
 * @param atom
 * @return explicitly used valence
 */
public int getOccupiedValence(int atom) {
	return oclMolecule.getOccupiedValence(atom);
}

/**
 * The sum of bond orders of explicitly connected neighbour atoms with the cAtomQFExcludeGroup flag set to true.
 * @param atom
 * @return occupied valence caused by exclude group atoms
 */
public int getExcludeGroupValence(int atom) {
	return oclMolecule.getExcludeGroupValence(atom);
}

/**
 * The free valence is the number of potential additional single bonded
 * neighbours to reach the atom's maximum valence. Atomic numbers that have
 * multiple possible valences, the highest value is taken.
 * Atom charges are considered. Implicit hydrogens are not considered.
 * Thus, the oxygen in a R-O(-) has a free valence of 0, the nitrogen in R3N(+)
 * has a free valence of 1. Chlorine in Cl(-) has a free valence of 6. If you need
 * the free valence taking the lowest possible valence into account, use
 * getLowestFreeValence(), which would return 0 for Cl(-).
 * @param atom
 * @return
 */
public int getFreeValence(int atom) {
	return oclMolecule.getFreeValence(atom);
}

/**
 * The free valence is the number of potential additional single bonded
 * neighbours to reach the atom's lowest valence above or equal its current
 * occupied valence. Atom charges are considered. Implicit hydrogens are not considered.
 * Thus, the phosphor atoms in PF2 and PF4 both have a lowest free valence of 1.
 * The oxygen in R-O(-) has a lowest free valence of 0, the nitrogen in R3N(+)
 * has a free valence of 1. If you need the maximum possible free valence,
 * use getFreeValence(), which would give 6 for Cl(-) and HCl.
 * @param atom
 * @return
 */
public int getLowestFreeValence(int atom) {
	return oclMolecule.getLowestFreeValence(atom);
}

/**
 * If the explicitly attached neighbors cause an atom valence to exceed
 * the lowest allowed valence for this atomic no, then this method returns
 * the next higher allowed valence, e.g. O=P(-H)-OMe :<br>
 * standard P valence is 3, used valence is 4, implicit abnormal valence is 5.
 * The molecule is interpreted as O=PH2-OMe. Requires cHelperNeighbours!
 * @param atom
 * @param neglectExplicitHydrogen
 * @return abnormal valence or -1 if valence doesn't exceed standard valence
 */
public int getImplicitHigherValence(int atom, boolean neglectExplicitHydrogen) {
	return oclMolecule.getImplicitHigherValence(atom, neglectExplicitHydrogen);
}

/**
 * Calculates for every non-H atom the mean value of all shortest routes (bonds in between)
 * to any other atom of the same fragment.
 * @return
 */
public float[] getAverageTopologicalAtomDistance() {
	return oclMolecule.getAverageTopologicalAtomDistance();
}

/**
 * Calculates the length of the shortest path between atoms atom1 and atom2
 * @param atom1
 * @param atom2
 * @return path length (no of bonds); -1 if there is no path
 */
public int getPathLength(int atom1, int atom2) {
	return oclMolecule.getPathLength(atom1, atom2);
}


/**
 * Locates and returns the shortest path between atoms atom1 and atom2
 * @param pathAtom array large enough to hold all path atoms, i.e. maxLength+1
 * @param atom1 first atom of path; ends up in pathAtom[0]
 * @param atom2 last atom of path; ends up in pathAtom[pathLength]
 * @param maxLength paths larger than maxLength won't be detected
 * @param neglectBond null or bitmask of forbidden bonds
 * @return number of bonds of path; -1 if there is no path
 */
public int getPath(int[] pathAtom, int atom1, int atom2, int maxLength, boolean[] neglectBond) {
	return oclMolecule.getPath(pathAtom, atom1, atom2, maxLength, neglectBond);
}

/**
 * Finds bonds of a path that is defined by an atom sequence.
 * @param pathAtom pathAtom[0]...[pathLength] -> list of atoms on path
 * @param pathBond int array not smaller than pathLength
 * @param pathLength no of path bonds == no of path atoms - 1
 */
public void getPathBonds(int[] pathAtom, int[] pathBond, int pathLength) {
	oclMolecule.getPathBonds(pathAtom, pathBond, pathLength);
}

/**
 * @param atom1
 * @param atom2
 * @return whether there is a path of bonds leading from atom1 to atom2
 */
public boolean shareSameFragment(int atom1, int atom2) {
	return oclMolecule.shareSameFragment(atom1, atom2);
}

/**
 * This adds a fragment from sourceMol to this molecule by first copying rootAtom and then
 * all connected atoms and bonds by traversing the graph breadth first.
 * @param sourceMol molecule from which the fragment is copied to this
 * @param rootAtom
 * @param atomMap null or int[] not smaller than sourceMol.mAllAtoms; receives atom indices of this molecule
 */
public void addFragment(JSMolecule sourceMol, int rootAtom, int[] atomMap) {
	oclMolecule.addFragment(sourceMol.getStereoMolecule(), rootAtom, atomMap);
}

/**
 * Returns an array of all atoms for which a path of bonds leads to rootAtom.
 * Metal ligand bonds may or may not be considered a connection.
 * @param rootAtom
 * @param considerMetalBonds
 * @return atoms being in the same fragment as rootAtom
 */
public int[] getFragmentAtoms(int rootAtom, boolean considerMetalBonds) {
	return oclMolecule.getFragmentAtoms(rootAtom, considerMetalBonds);
}

/**
 * Locates all unconnected fragments in the Molecule and assigns fragment indexes
 * for every atom starting with 0. Optionally the fragment detection may be restricted to
 * those atoms that have been previously marked with setAtomMarker(). In that case
 * non-marked atoms receive the fragment number -1 and are not considered a connection between
 * marked atoms potentially causing two marked atoms to end up in different fragments, despite
 * sharing the same fragment.
 * Metal ligand bonds may or may not be considered a connection.
 * @param fragmentNo array at least mAllAtoms big to receive atom fragment indexes
 * @param markedAtomsOnly if true, then only atoms marked with setAtomMarker() are considered
 * @param considerMetalBonds
 * @return number of disconnected fragments
 */
public int getFragmentNumbers(int[] fragmentNo, boolean markedAtomsOnly, boolean considerMetalBonds) {
	return oclMolecule.getFragmentNumbers(fragmentNo, markedAtomsOnly, considerMetalBonds);
}

/**
 * Removes all unconnected fragments except for the largest one.
 * If small fragments were removed, then canonizeCharge() is called to
 * neutralize charges after potential removal of counter ions.
 * Metal ligand bonds may or may not be considered a connection.
 * @param considerMetalBonds
 * @return atom mapping from old to new index; null if no fragments were removed
 */
public int[] stripSmallFragments(boolean considerMetalBonds) {
	return oclMolecule.stripSmallFragments(considerMetalBonds);
}

/**
 * Starting from startAtom this method locates a system of annelated or bridged ring systems
 * with all members bonds being a ring bond. Detected member atoms and bonds are flagged
 * accordingly.
 * @param startAtom
 * @param aromaticOnly if set then only aromatic atoms and bonds are considered
 * @param isMemberAtom
 * @param isMemberBond
 */
public void findRingSystem(int startAtom, boolean aromaticOnly, boolean[] isMemberAtom, boolean[] isMemberBond) {
	oclMolecule.findRingSystem(startAtom, aromaticOnly, isMemberAtom, isMemberBond);
}

/**
 * Determines all atoms of the substituent attached to coreAtom and starting
 * with firstAtom. If isMemberAtom!=null, then all substituent member atoms
 * will have the the respective index being flagged upon return. This includes
 * firstAtom and excludes coreAtom.
 * If substituent!=null, then it will contain the substituent as Molecule.
 * At the position of the coreAtom substituent will contain a wildcard atom.
 * If substituent!=null and atomMap!=null then atomMap receives atom index mapping from
 * this to substituent with non-member atoms being -1.
 * Returns -1 and an empty substituent if coreAtom and firstAtom share a ring
 * @param coreAtom the atom to which the substituent is connected
 * @param firstAtom the substituent's atom that is connected to coreAtom
 * @param isMemberAtom may be null, otherwise set to contain atom membership mask
 * @param substituent may be null, otherwise set to contain the substituent
 * @param atomMap null or int[] not smaller than this.getAllAtoms()
 * @return substituent atom count not counting coreAtom; -1 if coreAtom and firstAtom share a ring
 */
public int getSubstituent(int coreAtom, int firstAtom, boolean[] isMemberAtom, JSMolecule substituent, int[] atomMap) {
	return oclMolecule.getSubstituent(coreAtom, firstAtom, isMemberAtom, substituent.getStereoMolecule(), atomMap);
}

/**
 * Counts the number of atoms of the substituent connected to coreAtom
 * defined by firstAtom and not including the coreAtom.
 * @param coreAtom
 * @param firstAtom
 * @return atom count of substituent or -1 if coreAtom and firstAtom are in the same ring
 */
public int getSubstituentSize(int coreAtom, int firstAtom) {
	return oclMolecule.getSubstituentSize(coreAtom, firstAtom);
}

/**
 * Whether an atom may be considered to carry implicit hydrogen atoms depends
 * on the atomicNo of that atom. Aluminum and all non/metal atoms except the
 * nobel gases and except hydrogen itself are considered to carry implicit hydrogens
 * to fill up their unoccupied valences. Atoms with an assigned unusual valence always
 * support implicit hydrogens independent of their atomicNo.
 * @param atom
 * @return true if this atom's unoccupied valences are considered to be implicit hydrogens
 */
public boolean supportsImplicitHydrogen(int atom) {
	return oclMolecule.supportsImplicitHydrogen(atom);
}

/**
 * Calculates and return the number of implicit hydrogens at atom.
 * If atom is itself a hydrogen atom, a metal except Al, or a noble gase,
 * then 0 is returned. For all other atom kinds the number of
 * implicit hydrogens is basically the lowest typical valence that is compatible
 * with the occupied valence, minus the occupied valence corrected by atom charge
 * and radical state.
 * @param atom
 * @return
 */
public int getImplicitHydrogens(int atom) {
	return oclMolecule.getImplicitHydrogens(atom);
}

/**
 * @param atom
	 * @return number of explicit plain hydrogen atoms (does not include D,T,custom labelled H, etc)
 */
public int getExplicitHydrogens(int atom) {
	return oclMolecule.getExplicitHydrogens(atom);
}

/**
 * Calculates a rounded mass of the molecule
 * @return
 */
public int getMolweight() {
	return oclMolecule.getMolweight();
}

/**
 * Simple method to calculate rotatable bonds. This method counts all single
 * bonds provided that they<br>
 * - are not a terminal bond<br>
 * - are not part of a ring<br>
 * - are not an amide bond<br>
 * - are not the second of two equivalent bonds next to the same triple bond<br>
 * @return
 */
public int getRotatableBondCount() {
	return oclMolecule.getRotatableBondCount();
}

/**
 * In a consecutive sequence of sp-hybridized atoms multiple single bonds
 * cause redundant torsions. Only that single bond with the smallest bond index
 * is considered really rotatable; all other single bonds are pseudo rotatable.
 * If one/both end(s) of the sp-atom sequence doesn't carry atoms
 * outside of the straight line then no bond is considered rotatable.
 * A simple terminal single bond
 * @param bond
 * @return true, if this bond is not considered rotatable because of a redundancy
 */
public boolean isPseudoRotatableBond(int bond) {
	return oclMolecule.isPseudoRotatableBond(bond);
}


public int getAromaticRingCount() {
	return oclMolecule.getAromaticRingCount();
}

/**
 * Calculates the number of independent rings of which 'atom' is a member.
 * Any combination of two connected atoms to 'atom' is used for:
 * - finding the shortest path connecting these two neighbors avoiding 'atom'
 * - if such a path exists and at least one bonds of that path is not a member
 *   of a path found earlier then count this path as an independent ring closure.
 * @param atom
 * @param maxRingSize
 * @return number of independent rings
 */
public int getAtomRingCount(int atom, int maxRingSize) {
	return oclMolecule.getAtomRingCount(atom, maxRingSize);
}

/**
 * Locates that single bond which is the preferred one to be converted into up/down bond
 * in order to define the atom chirality.
 * @param atom parity carrying atom, i.e. a tetrahedral stereocenter or central allene atom
 * @return preferred bond or -1, if no single bond existing
 */
public int getAtomPreferredStereoBond(int atom) {
	return oclMolecule.getAtomPreferredStereoBond(atom);
}

/**
 * Locates that single bond which is the preferred one to be converted into up/down bond
 * in order to define the bond chirality.
 * @param bond BINAP type of chirality bond
 * @return preferred bond or -1, if no single bond existing
 */
public int getBondPreferredStereoBond(int bond) {
	return oclMolecule.getBondPreferredStereoBond(bond);
}

/**
 * @param atom
 * @return whether the atom is in an allylic/benzylic position
 */
public boolean isAllylicAtom(int atom) {
	return oclMolecule.isAllylicAtom(atom);
}


public boolean isAromaticAtom(int atom) {
	return oclMolecule.isAromaticAtom(atom);
}


public boolean isAromaticBond(int bnd) {
	return oclMolecule.isAromaticBond(bnd);
}

/**
 * A bond is considered delocalized, if it has different bond orders in
 * different, but energetically equivalent mesomeric structures. Bonds in aromatic 6-membered
 * rings typically are delocalized, while those in uncharged 5-membered aromatic rings are not.
 * Indole has 6 delocalized bonds.
 * @param bond
 * @return
 */
public boolean isDelocalizedBond(int bond) {
	return oclMolecule.isDelocalizedBond(bond);
}


public boolean isRingAtom(int atom) {
	return oclMolecule.isRingAtom(atom);
}


public boolean isRingBond(int bnd) {
	return oclMolecule.isRingBond(bnd);
}

/**
 * @param atom
 * @return whether atom is a member of a ring not larger than 7 atoms
 */
public boolean isSmallRingAtom(int atom) {
	return oclMolecule.isSmallRingAtom(atom);
}

/**
 * @param bond
 * @return whether bond is a member of a ring not larger than 7 atoms
 */
public boolean isSmallRingBond(int bond) {
	return oclMolecule.isSmallRingBond(bond);
}

/**
 * @param atom
 * @return whether atom has a neighbor that is connected through a double/triple bond to a hetero atom
 */
public boolean isStabilizedAtom(int atom) {
	return oclMolecule.isStabilizedAtom(atom);
}


public int getAtomRingBondCount(int atom) {
	return oclMolecule.getAtomRingBondCount(atom);
}


public String getChiralText() {
	return oclMolecule.getChiralText();
}

/**
 * Checks whether at least one of the connected bonds is a stereo bond.
 * If atom is the central atom of an allene, then its direct neighbours
 * are checked, whether one of them has a stereo bond.
 * @param atom
 * @return the stereo bond or -1 if not found
 */
public int getStereoBond(int atom) {
	return oclMolecule.getStereoBond(atom);
}

/**
 * Atom stereo parities and bond E/Z-parities are properties that are usually perceived
 * from up/down-bonds and atom coordinates, respectively. This is done during the helper
 * array calculation triggered by ensureHelperArrays(cHelperParities).<br>
 * This method tells the molecule that current atom/bond parities are valid, even if the
	 * stereo perception not has been performed. In addition to the stereo parities one may
 * declare CIP parities and/or symmetry ranks also to be valid (helperStereoBits != 0).
	 * setParitiesValid(0) should be called if no coordinates are available but the parities are valid
	 * nevertheless, e.g. after the IDCodeParser has parsed an idcode without coordinates.
 * (Note: After idcode parsing unknown stereo centers have parities cAtomParityNone
 * instead of cAtomParityUnknown. Thus, calling isStereoCenter(atom) returns false!!!)
 * Declaring parities valid prevents the Canonizer to run the stereo recognition again when
 * ensureHelperArrays(cHelperParities or higher) is called.<br>
 * May also be called after filling valences with explicit hydrogen atoms, which have no
 * coordinates, to tell the molecule that the earlier created stereo flags are still valid.
 * @param helperStereoBits 0 or combinations of cHelperBitCIP,cHelperBitSymmetry...,cHelperBitIncludeNitrogenParities
 */
public void setParitiesValid(int helperStereoBits) {
	oclMolecule.setParitiesValid(helperStereoBits);
}

/**
 * This converts one single bond per parity into a stereo up/down bond to
 * correctly reflect the given parity. This works for tetrahedral and
 * allene atom parities as well as for BINAP type of bond parities.
 * Should only be called with valid TH and EZ parities and valid coordinates,
 * e.g. after idcode parsing with coordinates or after coordinate generation.
 */
public void setStereoBondsFromParity() {
	oclMolecule.setStereoBondsFromParity();
}

/**
 * Converts any stereo bond attached with its pointed tip
 * to this atom into a single bond.
 * @param atom
 */
public void convertStereoBondsToSingleBonds(int atom) {
	oclMolecule.convertStereoBondsToSingleBonds(atom);
}


public void setStereoBondFromAtomParity(int atom) {
	oclMolecule.setStereoBondFromAtomParity(atom);
}

/**
 * If the atom is a stereo center in fisher projection, then its
 * tetrahedral parity is returned. If the horizontal bonds are plain
 * single bonds, then they are interpreted as up-bonds.
 * @param atom the stereo center
 * @param sortedConnMap map of neighbours sorted by atom index
 * @param angle bond angles sorted by neighbour atom index
 * @param direction null or int[] large enough to receive bond directions
 * @return cAtomParity1,cAtomParity2 or cAtomParityUnknown
 */
public int getFisherProjectionParity(int atom, int[] sortedConnMap, double[] angle, int[] direction) {
	return oclMolecule.getFisherProjectionParity(atom, sortedConnMap, angle, direction);
}

/**
 * In case bond is a BINAP kind of chiral bond with defined parity,
 * then the preferred neighbour single bond is converted into a
 * stereo bond to correctly reflect its defined parity.
 * @param bond
 */
public void setStereoBondFromBondParity(int bond) {
	oclMolecule.setStereoBondFromBondParity(bond);
}

/**
 * Checks whether atom is one of the two end of an allene.
 * @param atom
 * @return allene center or -1
 */
public int findAlleneCenterAtom(int atom) {
	return oclMolecule.findAlleneCenterAtom(atom);
}

/**
 * Checks whether atom is one of the two atoms of an axial chirality bond of BINAP type.
 * Condition: non-aromatic single bond connecting two aromatic rings with 6 or more members
 * that together bear at least three ortho substituents. A stereo bond indicating the
 * chirality is not(!!!) a condition.
 * @param atom to check, whether it is part of a bond, which has BINAP type of axial chirality
 * @return axial chirality bond or -1 if axial chirality conditions are not met
 */
public int findBINAPChiralityBond(int atom) {
	return oclMolecule.findBINAPChiralityBond(atom);
}

/**
 * Evaluates, whether bond is an amide bond, thio-amide, or amidine bond.
 * @param bond
 * @return
 */
public boolean isAmideTypeBond(int bond) {
	return oclMolecule.isAmideTypeBond(bond);
}

/**
 * Checks whether this nitrogen atom is flat, because it has a double bond,
 * is member of an aromatic ring or is part of amide, an enamine or
 * in resonance with an aromatic ring. It is also checked that ortho
 * substituents don't force the amine into a non-resonance torsion.
 * State of helper arrays must be at least cHelperRings.
 * @param atom
 * @return
 */
public boolean isFlatNitrogen(int atom) {
	return oclMolecule.isFlatNitrogen(atom);
}

/**
 * Checks whether bond is an axial chirality bond of the BINAP type.
 * Condition: non-aromatic, non-small-ring (<= 7 members) single bond
 * connecting two aromatic rings with 6 or more members each
 * that together bear at least three ortho substituents. A stereo bond indicating the
 * chirality is not(!!!) a condition.
 * @param bond
 * @return true if axial chirality conditions are met
 */
public boolean isBINAPChiralityBond(int bond) {
	return oclMolecule.isBINAPChiralityBond(bond);
}


public void validate() throws Exception {
	oclMolecule.validate();
}

/**
 * Normalizes different forms of functional groups (e.g. nitro)
 * to a preferred one. This step should precede any canonicalization.
 * @return true if the molecule was changed
 */
public boolean normalizeAmbiguousBonds() {
	return oclMolecule.normalizeAmbiguousBonds();
}

	/**
	 * @param atom
	 * @return whether atom is one of Li,Na,K,Rb,Cs
	 */
	public boolean isAlkaliMetal(int atom) {
		return oclMolecule.isAlkaliMetal(atom);
	}
	
	/**
	 * @param atom
	 * @return whether atom is one of Mg,Ca,Sr,Ba
	 */
	public boolean isEarthAlkaliMetal(int atom) {
		return oclMolecule.isEarthAlkaliMetal(atom);
	}
	
	/**
	 * @param atom
	 * @return whether atom is one of N,P,As
	 */
	public boolean isNitrogenFamily(int atom) {
		return oclMolecule.isNitrogenFamily(atom);
	}
	
	/**
	 * @param atom
	 * @return whether atom is one of O,S,Se,Te
	 */
	public boolean isChalcogene(int atom) {
		return oclMolecule.isChalcogene(atom);
	}
	
	/**
	 * @param atom
	 * @return whether atom is one of F,Cl,Br,I
	 */
	public boolean isHalogene(int atom) {
		return oclMolecule.isHalogene(atom);
	}
	
/**
 * Canonizes charge distribution in single- and multifragment molecules.
 * Neutralizes positive and an equal amount of negative charges on electronegative atoms,
 * provided these are not on 1,2-dipolar structures, in order to ideally achieve a neutral molecule.
 * This method does not change the overall charge of the molecule. It does not change the number of
 * explicit atoms or bonds or their connectivity except bond orders.
 * @return remaining overall molecule charge
 */
public int canonizeCharge(boolean allowUnbalancedCharge) throws Exception {
	return oclMolecule.canonizeCharge(allowUnbalancedCharge);
}

/**
 * Provided that the bond parity of a double bond is available,
 * this method determines, whether connAtom has a counterpart with
 * Z- (cis) configuration at the other end of the double bond.
 * If there is no Z-counterpart, then -1 is returned.
 * Requires cHelperParities.
 * @param connAtom directly connected to one of the double bond atoms
 * @param bond double bond with available bond parity
 * @return -1 or counterpart to connAtom in Z-configuration
 */
public int getZNeighbour(int connAtom, int bond) {
	return oclMolecule.getZNeighbour(connAtom, bond);
}


public int getHelperArrayStatus() {
	return oclMolecule.getHelperArrayStatus();
}

/**
 * While the Molecule class covers all primary molecule information, its derived class
 * ExtendedMolecule handles secondary, i.e. calculated molecule information, which is cached
 * in helper arrays and stays valid as long as the molecule's primary information is not changed.
 * Most methods of ExtendedMolecule require some of the helper array's information. High level
 * methods, e.g. getPath(), take care of updating an outdated cache themselves. Low level methods,
 * e.g. isAromaticAtom(), which typically are called very often, do not check for validity
 * nor update the helper arrays themselves. If you use low level methods, then you need to make
 * sure that the needed helper array information is valid by this method.<br>
 * For performance reasons there are <b>distinct levels of helper information</b>. (A higher
 * level always includes all properties of the previous level):<br>
 * <i>cHelperNeighbours:</i> explicit hydrogen atoms are moved to the end of the atom table and
 * bonds leading to them are moved to the end of the bond table. This way algorithms can skip
 * hydrogen atoms easily. For every atom directly connected atoms and bonds (with and without
 * hydrogens) are determined. The number of pi electrons is counted.<br>
 * <i>cHelperRings</i>: Aromatic and non-aromatic rings are detected. Atom and bond ring
 * properties are set and a ring collection provides a total set of small rings (7 or less atoms).
 * Atoms being in allylic/benzylic or stabilized (neighbor of a carbonyl or similar group) position
 * are flagged as such.<br>
 * <i>cHelperParities</i>: Atom (tetrahedral or axial) and bond (E/Z or atrop) parities are calculated
 * from the stereo configurations.<br>
 * <i>cHelperCIP</i>: Cahn-Ingold-Prelog stereo information for atoms and bonds.<br>
 * <br>cHelperParities and cHelperCIP require a StereoMolecule!!!<br>
 * @param required one of cHelperNeighbours,cHelperRings,cHelperParities,cHelperCIP
 * @return true if the molecule was changed
 */
public void ensureHelperArrays(int required) {
	oclMolecule.ensureHelperArrays(required);
}

/**
 * If ensureHelperArrays() (and with it handleHydrogens()) was not called yet
 * on a fresh molecule and if the molecule contains simple hydrogen atoms within
 * non-hydrogens atoms, then this function returns a map from current atom indexes
 * to those new atom indexes that would result from a call to handleHydrogens.
 * @return
 */
public int[] getHandleHydrogenMap() {
	return oclMolecule.getHandleHydrogenMap();
}

/**
 * Uncharged hydrogen atoms with no isotop information nor with an attached custom label
 * are considered simple and can usually be suppressed, effectively converting them from an
 * explicit to an implicit hydrogen atom.<br>
 * <b>Note:</b> This method returns true for uncharged, natural abundance hydrogens without
 * custom labels even if they have a non-standard bonding situation (everything being different
 * from having one single bonded non-simple-hydrogen neighbour, e.g. unbonded hydrogen, H2,
 * a metal ligand bond to another atom, two single bonds, etc.)
 * If unusual bonding needs to be considered, check for that independently from this method.
 * @param atom
 * @return
 */
public boolean isSimpleHydrogen(int atom) {
	return oclMolecule.isSimpleHydrogen(atom);
}

/**
 * Removes all plain explicit hydrogens atoms from the molecule, converting them
 * effectively to implicit ones. If an associated bond is a stereo bond indicating
 * a specific configuration, then another bond is converted to a stereo bond to reflect
 * the correct stereo geometry. If the removal of a hydrogen atom would change an atom's
 * implicit valance, the atom's abnormal valence is set accordingly.
 */
public void removeExplicitHydrogens() {
	oclMolecule.removeExplicitHydrogens();
}

/**
 * Separates all disconnected fragments of this Molecule into individual Molecule objects.
 * If fragment separation is only needed, if there are multiple fragments, it may be more
 * efficient to run this functionality in two steps, e.g.:<br>
 * int[] fragmentNo = new int[mol.getAllAtoms()];<br>
 * int fragmentCount = getFragmentNumbers(fragmentNo, boolean, boolean);<br>
 * if (fragmentCount > 1) {<br>
	 *     StereoMolecule[] fragment = getUniqueFragmentsEstimated(int[] fragmentNo, fragmentCount);<br>
 *     ...<br>
 *     }<br>
 * @return
 */
public JSMolecule[] getFragments() {
		StereoMolecule[] fragments = oclMolecule.getFragments();
		JSMolecule[] newFragments = new JSMolecule[fragments.length];
		for(int i = 0; i < fragments.length; i++) {
			newFragments[i] = new JSMolecule(fragments[i]);
		}
		return newFragments;

}
	
/**
 * Removes defined and implicit stereo information from the molecule.<br>
 * - up/down-bonds are converted to double bonds<br>
 * - stereo centers are flagged to be unknown<br>
 * - double bonds with implicit stereo configurations are converted into cross bonds<br>
 * - all atom and bond ESR assignments are removed<br>
 * - parity and CIP helper state is set to invalid, such that stereo calculation is redone, if needed.
 */
public void stripStereoInformation() {
	oclMolecule.stripStereoInformation();
}

/**
    * This returns the absolute(!) atom parity from the canonization procedure.
    * While the molecule's (relative) atom parity returned by getAtomParity() is
    * based on atom indices and therefore depends on the order of atoms,
    * the absolute atom parity is based on atom ranks and therefore independent
    * of the molecule's atom order.
    * Usually relative parities are used, because the atom's stereo situation
    * can be interpreted without the need for atom rank calculation.
    * This requires valid helper arrays level cHelperParities or higher.
    * @param atom
    * @return one of the Molecule.cAtomParityXXX constants
    */
public int getAbsoluteAtomParity(int atom) {
	return oclMolecule.getAbsoluteAtomParity(atom);
}

   /**
    * This returns the absolute(!) bond parity from the canonization procedure.
    * While the molecule's (relative) bond parity returned by getBondParity() is
    * based on atom indices and therefore depends on the order of atoms,
    * the absolute bond parity is based on atom ranks and therefore independent
    * of the molecule's atom order.
    * Usually relative parities are used, because the bond's stereo situation
    * can be interpreted without the need for atom rank calculation.
    * This requires valid helper arrays level cHelperParities or higher.
    * @param bond
    * @return one of the Molecule.cBondParityXXX constants
    */
public int getAbsoluteBondParity(int bond) {
	return oclMolecule.getAbsoluteBondParity(bond);
}

   /**
    * This returns atom symmetry numbers from within the molecule
    * canonicalization procedure. Atoms with same symmetry numbers
    * can be considered topologically equivalent. Symmetry ranks are
    * only available after calling ensureHelperArrays(cHelperSymmetry...).
    * In mode cHelperSymmetrySimple stereoheterotopic atoms are considered
    * equivalent. In mode cHelperSymmetryDiastereotopic only diastereotopic
    * atoms are distinguished. In mode cHelperSymmetryEnantiotopic all
    * stereoheterotopic atoms, i.e. enantiotopic and diastereotopic atoms,
    * are distinguished.
    */
public int getSymmetryRank(int atom) {
	return oclMolecule.getSymmetryRank(atom);
}

   /**
    * This is a convenience method that creates the molecule's idcode
    * without explicitly creating a Canonizer object for this purpose.
    * The idcode is a compact String that uniquely encodes the molecule
    * with all stereo and query features.
    * <br>WARNING: If the molecule has no atom coordinates but valid parities,
    * e.g. after new IDCodeParser(false).parse(idcode, null), this method returns null;
    * @return
    */
public String getIDCode() {
	return oclMolecule.getIDCode();
}

   /**
    * This is a convenience method that creates the molecule's id-coordinates
    * matching the idcode available with getIDCode().
    * It does not explicitly create a Canonizer object for this purpose.
    * <br>WARNING: If the molecule has no atom coordinates but valid parities,
    * e.g. after new IDCodeParser(false).parse(idcode, null), this method returns null;
    * @return
    */
public String getIDCoordinates() {
	return oclMolecule.getIDCoordinates();
}

public int getStereoCenterCount() {
	return oclMolecule.getStereoCenterCount();
}

/**
 * Sets all atoms with TH-parity 'unknown' to explicitly defined 'unknown'.
 * Sets all double bonds with EZ-parity 'unknown' to cross bonds.
 */
public void setUnknownParitiesToExplicitlyUnknown() {
	oclMolecule.setUnknownParitiesToExplicitlyUnknown();
}

   /**
    * This is a policy setting for this StereoMolecule as molecule container.
    * If set to true then this StereoMolecule will treat tetrahedral nitrogen atoms
    * with three or four distinguishable substituents as stereo centers and will
    * assign parities. deleteMolecule() does not change this behavior.
    * @param b
    */
public void setAssignParitiesToNitrogen(boolean b) {
	oclMolecule.setAssignParitiesToNitrogen(b);
}
	// END
}
