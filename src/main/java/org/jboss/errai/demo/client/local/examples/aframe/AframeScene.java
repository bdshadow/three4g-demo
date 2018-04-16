package org.jboss.errai.demo.client.local.examples.aframe;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.Element;

/**
 * @author Dmitrii Tikhomirov <chani@me.com>
 * Created by treblereel on 4/16/18.
 */
@Element("a-scene")
@JsType(isNative = true, name = "HTMLElement", namespace = JsPackage.GLOBAL)
public class AframeScene extends Object3D {

    @JsOverlay
    final double getHeight(){
        return Double.valueOf(getAttribute("height"));
    }

    @JsOverlay
    final void setHeight(double height){
        setAttribute("height", height);

    }

    @JsOverlay
    final double getWidth(){
        return Double.valueOf(getAttribute("width"));
    }

    @JsOverlay
    final void setWidth(double width){
        setAttribute("width", width);
    }

}
