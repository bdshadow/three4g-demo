package org.jboss.errai.demo.client.local.gui;

import jsinterop.annotations.JsType;

/**
 * @author Dmitrii Tikhomirov <chani@me.com>
 *         Created by treblereel on 12/13/17.
 */
@JsType(isNative = true, namespace = "dat", name = "Group")
public class Group {


    public native Group name(String name);

}