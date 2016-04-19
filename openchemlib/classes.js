var classes = [
    // Requirements for Core
    'calc/ThreadMaster',

    'chem/AbstractDepictor',
    'chem/AromaticityResolver',
    'chem/AtomFunctionAnalyzer',
    'chem/AtomTypeCalculator',
    'chem/Canonizer',
    'chem/CanonizerBaseValue',
    'chem/CanonizerMesoHelper',
    'chem/CoordinateInventor',
    'chem/ExtendedMolecule',
    'chem/IDCodeParser',
    'chem/IsotopeHelper',
    'chem/MolecularFormula',
    'chem/Molecule',
    'chem/MolfileCreator',
    'chem/MolfileParser',
    'chem/PropertyCalculator',
    'chem/RingCollection',
    'chem/SmilesCreator',
    'chem/SmilesParser',
    'chem/SortedStringList',
    'chem/SSSearcher',
    'chem/SSSearcherWithIndex',
    'chem/StereoMolecule',
    'chem/SVGDepictor',
    'chem/UniqueStringList',

    'chem/contrib/DiastereotopicAtomID',
    'chem/contrib/HoseCodeCreator',
    'chem/contrib/HydrogenHandler',

    'chem/descriptor/DescriptorHandler',
    'chem/descriptor/DescriptorHandlerFactory',
    'chem/descriptor/DescriptorInfo',
    'chem/descriptor/ISimilarityCalculator',
    'chem/descriptor/ISimilarityHandlerFactory',
    'chem/descriptor/SimilarityCalculatorInfo',

    'chem/io/SDFileParser',
    'chem/io/CompoundFileParser',

    'chem/prediction/CLogPPredictor',
    'chem/prediction/DrugScoreCalculator', // TODO not in OCL
    'chem/prediction/PolarSurfaceAreaPredictor',
    'chem/prediction/ParameterizedStringList',
    'chem/prediction/SolubilityPredictor',

    'util/Angle',
    'util/ColorHelper',
    'util/SortedList',
    'util/IntArrayComparator',

    // Requirements for Viewer
    'share/gui/editor/geom/IDrawContext', // TODO not in OCL
    'share/gui/editor/geom/IPolygon', // TODO not in OCL

];

exports.copy = classes.map(getFilename);

var modified = [
    // Requirements for Core
    'chem/AbstractDrawingObject',
    'chem/DepictorTransformation',

    'chem/prediction/DruglikenessPredictor',
    'chem/prediction/IncrementTable',
    'chem/prediction/ToxicityPredictor',
];

exports.modified = modified.map(getFilename);

function getFilename(file) {
    return 'com/actelion/research/' + file + '.java';
}
