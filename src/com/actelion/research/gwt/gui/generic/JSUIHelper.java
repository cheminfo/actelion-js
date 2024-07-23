package com.actelion.research.gwt.gui.generic;

import com.actelion.research.gui.generic.*;
import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

import java.io.File;

@JsType(name = "GenericUIHelper")
public class JSUIHelper implements GenericUIHelper {
  private JavaScriptObject mJsObject;

  public JSUIHelper(JavaScriptObject jsObject) {
    mJsObject = jsObject;
    registerToJs();
  }

  private JavaScriptObject getJsObject() {
		return mJsObject;
	}

  private native void registerToJs()
  /*-{
    var jsObject = this.@com.actelion.research.gwt.gui.generic.JSUIHelper::getJsObject()();
    jsObject.register(this);
  }-*/;

  @Override
  public GenericDialog createDialog(String title, GenericEventListener<GenericActionEvent> consumer) {
    JSDialog dialog = new JSDialog(createNativeDialog(title));
    dialog.setEventConsumer(consumer);
    return dialog;
  }
  
  public native JavaScriptObject createNativeDialog(String title)
  /*-{
    var jsObject = this.@com.actelion.research.gwt.gui.generic.JSUIHelper::getJsObject()();
    return jsObject.createDialog(title);
  }-*/;

  @Override
  @JsIgnore
  public GenericImage createImage(String name) {
    if (name.equals("editorButtons.png")) {
      String data = ImageData.editorButtonsData0 + ImageData.editorButtonsData1 + ImageData.editorButtonsData2 + ImageData.editorButtonsData3 + ImageData.editorButtonsData4 + ImageData.editorButtonsData5;
      return new JSImage(createImageFromBase64(ImageData.editorButtonsWidth, ImageData.editorButtonsHeight, data));
    } else if (name.equals("esrButtons.png")) {
      return new JSImage(createImageFromBase64(ImageData.esrButtonsWidth, ImageData.esrButtonsHeight, ImageData.esrButtonsData0));
    } else {
      return null;
    }
  }

  private native JavaScriptObject createImageFromBase64(int width, int height, String base64)
  /*-{
    var jsObject = this.@com.actelion.research.gwt.gui.generic.JSUIHelper::getJsObject()();
    return jsObject.createImageFromBase64(width, height, base64);
  }-*/;

  @Override
  public native JSImage createImage(int width, int height)
  /*-{
    var jsObject = this.@com.actelion.research.gwt.gui.generic.JSUIHelper::getJsObject()();
    var image = jsObject.createImage(width, height);
    return @com.actelion.research.gwt.gui.generic.JSImage::new(Lcom/google/gwt/core/client/JavaScriptObject;)(image);
  }-*/;

  @Override
  public GenericPopupMenu createPopupMenu(GenericEventListener<GenericActionEvent> consumer) {
    // TODO: implement popup menu.
    return null;
  }

  @Override
  public native void grabFocus()
  /*-{
    var jsObject = this.@com.actelion.research.gwt.gui.generic.JSUIHelper::getJsObject()();
    return jsObject.grabFocus();
  }-*/;

  @Override
  public File openChemistryFile(boolean isReaction) {
    return null;
  }

  @Override
  public native void runLater(Runnable r)
  /*-{
    var that = this;
    $wnd.requestAnimationFrame(function(){
      that.@com.actelion.research.gwt.gui.generic.JSUIHelper::runNow(Ljava/lang/Runnable;)(r);
    });
  }-*/;

  private void runNow(Runnable r) {
    r.run();
  }

  @Override
  public native void setCursor(int cursor)
  /*-{
    var jsObject = this.@com.actelion.research.gwt.gui.generic.JSUIHelper::getJsObject()();
    return jsObject.setCursor(cursor);
  }-*/;

  @Override
  public native void showHelpDialog(String url, String title)
  /*-{
    var jsObject = this.@com.actelion.research.gwt.gui.generic.JSUIHelper::getJsObject()();
    return jsObject.showHelpDialog(url, title);
  }-*/;

  @Override
  public void showMessage(String message) {}

  public JavaScriptObject build16x16CursorImage(int cursor) {
    JSImage cursorImage = createImage(16, 16);
    GenericCursorHelper.build16x16CursorImage(cursorImage, cursor);
    return cursorImage.getJsImage();
  }
}
