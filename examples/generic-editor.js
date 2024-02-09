import { CanvasEditor } from './generic-editor/index.js';

const { Reaction, Molecule, EditorArea } = OCL;

const rxn = `$RXN


JME Molecular Editor
  2  3
$MOL
C1=CC=CC=C1.CC>N>C1=CC=CC=C1.CCC.CC
JME 2015-09-20 Mon Sep 28 16:02:56 GMT+200 2015

  6  6  0  0  0  0  0  0  0  0999 V2000
    2.4249    0.7000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    2.4249    2.1000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    2.8000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    0.0000    2.1000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    0.0000    0.7000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1  2  1  0  0  0  0
  2  3  2  0  0  0  0
  3  4  1  0  0  0  0
  4  5  2  0  0  0  0
  5  6  1  0  0  0  0
  6  1  2  0  0  0  0
M  END
$MOL
C1=CC=CC=C1.CC>N>C1=CC=CC=C1.CCC.CC
JME 2015-09-20 Mon Sep 28 16:02:56 GMT+200 2015

  2  1  0  0  0  0  0  0  0  0999 V2000
    0.0000    0.7000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1  2  1  0  0  0  0
M  END
$MOL
C1=CC=CC=C1.CC>N>C1=CC=CC=C1.CCC.CC
JME 2015-09-20 Mon Sep 28 16:02:56 GMT+200 2015

  6  6  0  0  0  0  0  0  0  0999 V2000
    2.4249    0.7000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    2.4249    2.1000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    2.8000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    0.0000    2.1000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    0.0000    0.7000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1  2  1  0  0  0  0
  2  3  2  0  0  0  0
  3  4  1  0  0  0  0
  4  5  2  0  0  0  0
  5  6  1  0  0  0  0
  6  1  2  0  0  0  0
M  END
$MOL
C1=CC=CC=C1.CC>N>C1=CC=CC=C1.CCC.CC
JME 2015-09-20 Mon Sep 28 16:02:56 GMT+200 2015

  3  2  0  0  0  0  0  0  0  0999 V2000
    0.0000    2.1000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    1.4000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1  2  1  0  0  0  0
  2  3  1  0  0  0  0
M  END
$MOL
C1=CC=CC=C1.CC>N>C1=CC=CC=C1.CCC.CC
JME 2015-09-20 Mon Sep 28 16:02:56 GMT+200 2015

  2  1  0  0  0  0  0  0  0  0999 V2000
    0.0000    0.7000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.2124    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1  2  1  0  0  0  0
M  END`;

const molfile = `446220
-OEChem-02022300542D

 43 45  0     1  0  0  0  0  0999 V2000
    5.5851   -0.3586    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
    6.5407   -2.1051    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
    8.2717   -2.0446    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
    4.9927    1.2690    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
    8.0971    2.2384    0.0000 N   0  0  3  0  0  0  0  0  0  0  0  0
    8.5749    0.2899    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0
    8.8337    1.2558    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0
    7.3538   -0.5758    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0
    9.9738   -0.2189    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
   10.2326    0.7470    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    6.8004    0.9447    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    6.5248   -0.0166    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0
    7.5971    3.1045    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    7.3888   -1.5752    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    4.8191    0.2842    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    6.5756   -3.1045    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    3.8794   -0.0578    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    3.7057   -1.0426    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    3.1133    0.5850    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    2.7660   -1.3846    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    2.1736    0.2429    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    2.0000   -0.7419    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    8.3549   -0.5311    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    9.0537    2.0769    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    6.8116   -0.8763    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    9.7789   -0.8075    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
   10.5678   -0.3967    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
   10.8266    0.5692    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
   10.4276    1.3356    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    6.7954    1.5647    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    6.1861    1.0285    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    6.4493   -0.6319    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    8.1340    3.4145    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    7.2871    3.6414    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    7.0601    2.7945    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    5.9560   -3.1261    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    6.5972   -3.7241    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    7.1952   -3.0828    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    4.1807   -1.4412    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    3.2210    1.1955    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    2.6584   -1.9952    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    1.6987    0.6415    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
    1.4174   -0.9539    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
 12  1  1  1  0  0  0
  1 15  1  0  0  0  0
  2 14  1  0  0  0  0
  2 16  1  0  0  0  0
  3 14  2  0  0  0  0
  4 15  2  0  0  0  0
  5  6  1  0  0  0  0
  5  7  1  0  0  0  0
  5 13  1  0  0  0  0
  6  8  1  0  0  0  0
  6  9  1  0  0  0  0
  6 23  1  1  0  0  0
  7 10  1  0  0  0  0
  7 11  1  0  0  0  0
  7 24  1  6  0  0  0
  8 12  1  0  0  0  0
  8 14  1  1  0  0  0
  8 25  1  0  0  0  0
  9 10  1  0  0  0  0
  9 26  1  0  0  0  0
  9 27  1  0  0  0  0
 10 28  1  0  0  0  0
 10 29  1  0  0  0  0
 11 12  1  0  0  0  0
 11 30  1  0  0  0  0
 11 31  1  0  0  0  0
 12 32  1  0  0  0  0
 13 33  1  0  0  0  0
 13 34  1  0  0  0  0
 13 35  1  0  0  0  0
 15 17  1  0  0  0  0
 16 36  1  0  0  0  0
 16 37  1  0  0  0  0
 16 38  1  0  0  0  0
 17 18  2  0  0  0  0
 17 19  1  0  0  0  0
 18 20  1  0  0  0  0
 18 39  1  0  0  0  0
 19 21  2  0  0  0  0
 19 40  1  0  0  0  0
 20 22  2  0  0  0  0
 20 41  1  0  0  0  0
 21 22  1  0  0  0  0
 21 42  1  0  0  0  0
 22 43  1  0  0  0  0
M  END`;

const changeCountDiv = document.getElementById('changeCount');
const idcodeDiv = document.getElementById('idcode');
const molfileDiv = document.getElementById('molfile');
let changeCount = 0;

const editor = new CanvasEditor(document.getElementById('editor'), {
  onChange({ what, isUserEvent }) {
    if (isUserEvent && what === EditorArea.EDITOR_EVENT_MOLECULE_CHANGED) {
      changeCountDiv.innerText = ++changeCount;
      const idcodeAndCoords = editor.getMolecule().getIDCodeAndCoordinates();
      idcodeDiv.innerText = `${idcodeAndCoords.idCode} ${idcodeAndCoords.coordinates}`;
      const molfile = editor.getMolecule().toMolfileV3();
      molfileDiv.innerText = molfile;
    }
  },
});

document.getElementById('loadMolecule').onclick = () => {
  // const molecule = Molecule.fromMolfile(molfile);
  const molecule = Molecule.fromSmiles('c1ccccc1CO');
  editor.setMolecule(molecule);
};

document.getElementById('loadFragment').onclick = () => {
  // const molecule = Molecule.fromMolfile(molfile);
  const molecule = Molecule.fromSmiles('CCC');
  molecule.setFragment(true);
  editor.setMolecule(molecule);
};

document.getElementById('loadReaction').onclick = () => {
  const reaction = Reaction.fromSmiles('c1ccccc1..CC>CO>c1ccccc1..CCC..CC');
  // const reaction = Reaction.fromRxn(rxn);
  // reaction.addCatalyst(Molecule.fromSmiles('CO'));
  console.log(reaction.toSmiles());
  editor.setReaction(reaction);
};
