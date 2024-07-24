'use strict';

const EditorArea = require('./editor_area');
const { addPointerListeners, addKeyboardListeners } = require('./events');
const Toolbar = require('./toolbar');
const UIHelper = require('./ui_helper');

function createEditor(
  parentElement,
  options,
  onChange,
  JavaEditorArea,
  JavaEditorToolbar,
  JavaUIHelper,
) {
  const { readOnly = false } = options;

  const rootElement = document.createElement('div');
  Object.assign(rootElement.style, {
    width: '100%',
    height: '100%',
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'start',
    backgroundColor: 'white',
    // Prevent side effects of pointer events, like scrolling the page or text
    // selection.
    touchAction: 'none',
    userSelect: 'none',
    webkitUserSelect: 'none',
  });

  const toolbarCanvas = document.createElement('canvas');
  rootElement.append(toolbarCanvas);

  const editorContainer = document.createElement('div');
  Object.assign(editorContainer.style, {
    width: '100%',
    height: '100%',
  });
  rootElement.append(editorContainer);

  const editorCanvas = document.createElement('canvas');
  editorCanvas.tabIndex = 0;
  Object.assign(editorCanvas.style, {
    outline: 'none',
  });
  editorContainer.append(editorCanvas);

  parentElement.append(rootElement);

  const containerSize = editorContainer.getBoundingClientRect();
  editorCanvas.width = containerSize.width;
  editorCanvas.height = containerSize.height;

  const uiHelper = new JavaUIHelper(new UIHelper(editorCanvas, JavaEditorArea));

  const editorArea = new JavaEditorArea(
    new EditorArea(editorCanvas, onChange),
    uiHelper,
  );

  uiHelper.setEditorArea(editorArea);

  editorArea.draw();

  const resizeObserver = new ResizeObserver(([entry]) => {
    editorCanvas.width = entry.contentRect.width;
    editorCanvas.height = entry.contentRect.height;
    editorArea.repaint();
  });
  resizeObserver.observe(editorContainer);

  const toolbar = readOnly
    ? null
    : new JavaEditorToolbar(editorArea, new Toolbar(toolbarCanvas));

  let removePointerListeners = null;
  let removeKeyboardListeners = null;
  let removeToolbarPointerListeners = null;

  if (readOnly) {
    toolbarCanvas.remove();
  } else {
    removePointerListeners = addPointerListeners(
      editorCanvas,
      editorArea,
      JavaEditorArea,
    );
    removeKeyboardListeners = addKeyboardListeners(
      editorCanvas,
      editorArea,
      JavaEditorArea,
    );
    removeToolbarPointerListeners = addPointerListeners(
      toolbarCanvas,
      toolbar,
      JavaEditorArea,
    );
  }

  function destroy() {
    rootElement.remove();
    resizeObserver.disconnect();
    removePointerListeners?.();
    removeKeyboardListeners?.();
    removeToolbarPointerListeners?.();
  }

  return { editorArea, toolbar, uiHelper, destroy };
}

module.exports = createEditor;