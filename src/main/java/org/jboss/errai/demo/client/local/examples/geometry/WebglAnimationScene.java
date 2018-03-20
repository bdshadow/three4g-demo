package org.jboss.errai.demo.client.local.examples.geometry;

import com.google.gwt.animation.client.AnimationScheduler;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.Window;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;
import org.jboss.errai.demo.client.api.OrbitControls;
import org.jboss.errai.demo.client.local.Attachable;
import org.jboss.errai.demo.client.local.examples.geometry.css.GeometryCssClientBundle;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.treblereel.gwt.three4g.animation.AnimationClip;
import org.treblereel.gwt.three4g.animation.AnimationMixer;
import org.treblereel.gwt.three4g.cameras.PerspectiveCamera;
import org.treblereel.gwt.three4g.core.Clock;
import org.treblereel.gwt.three4g.core.Color;
import org.treblereel.gwt.three4g.core.Object3D;
import org.treblereel.gwt.three4g.core.TraverseCallback;
import org.treblereel.gwt.three4g.geometries.PlaneBufferGeometry;
import org.treblereel.gwt.three4g.loaders.ObjectLoader;
import org.treblereel.gwt.three4g.loaders.OnLoadCallback;
import org.treblereel.gwt.three4g.materials.MeshPhongMaterial;
import org.treblereel.gwt.three4g.objects.Mesh;
import org.treblereel.gwt.three4g.renderers.WebGLRenderer;
import org.treblereel.gwt.three4g.renderers.WebGLRendererParameters;
import org.treblereel.gwt.three4g.scenes.Fog;
import org.treblereel.gwt.three4g.scenes.Scene;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static elemental2.dom.DomGlobal.document;

/**
 * @author Dmitrii Tikhomirov <chani@me.com>
 * Created by treblereel on 3/9/18.
 */
@Templated(value = "webglAnimationScene.html#container1")
public class WebglAnimationScene extends Attachable {

    @Inject
    @DataField
    HTMLDivElement root, container1;

    OrbitControls controls;
    AnimationMixer mixer;
    AnimationClip animationClip;

    Clock clock = new Clock();

    Window window = DomGlobal.window;

    final static String URL = "json/scene-animation.json";

    @PostConstruct
    public void init() {
        GeometryCssClientBundle.IMPL.webglAnimationScene().ensureInjected();

        WebGLRendererParameters webGLRendererParameters = new WebGLRendererParameters();
        webGLRendererParameters.antialias = true;

        webGLRenderer = new WebGLRenderer(webGLRendererParameters);
        setupWebGLRenderer(webGLRenderer);
        // Load a scene with objects, lights and camera from a JSON file
        new ObjectLoader().load(URL, new OnLoadCallback<Object>() {

            @Override
            public void onLoad(Object object) {
                scene = (Scene) object;
                scene.background = new Color(0xffffff);

                JsPropertyMap<Object> javaScriptObject = Js.cast(object);
                JsArrayLike<AnimationClip> array = Js.cast(javaScriptObject.get("animations"));

                // If the loaded file contains a perspective camera, use it with adjusted aspect ratio...
                scene.traverse(new TraverseCallback() {
                    @Override
                    public void onEvent(Object3D object) {
                        if (object instanceof PerspectiveCamera) {
                            camera = (PerspectiveCamera) object;
                            camera.aspect = aspect;
                            camera.updateProjectionMatrix();
                        }
                    }
                });
                if (camera == null) {
                    camera = new PerspectiveCamera(30, aspect, 1, 10000);
                    camera.position.set(-200, 0, 200);
                }

                controls = new OrbitControls(camera);
                PlaneBufferGeometry geometry = new PlaneBufferGeometry(20000, 20000);
                MeshPhongMaterial material = new MeshPhongMaterial();
                material.shininess = 0.1f;

                Mesh ground = new Mesh(geometry, material);

                ground.position.set(0, -250, 0);
                ground.rotation.x = (float) -Math.PI / 2;
                scene.add(ground);
                scene.fog = new Fog(0xffffff, 1000, 10000);

                animationClip = array.getAt(0);
                mixer = new AnimationMixer(scene);
                mixer.clipAction(animationClip).play();

            }
        }, (e) -> new IllegalArgumentException(e.responseText), () -> new IllegalArgumentException("OnErrorCallback"));

        window.addEventListener("resize", evt -> onWindowResize(), false);
    }

    public void doAttachScene() {
        document.body.appendChild(webGLRenderer.domElement);
        onWindowResize();
        animate();
    }

    @Override
    protected void doAttachInfo() {

    }

    private void render() {
        mixer.update(0.75 * clock.getDelta());
        webGLRenderer.render(scene, camera);
    }

    private void animate() {
        AnimationScheduler.get().requestAnimationFrame(timestamp -> {
            if (webGLRenderer.domElement.parentNode != null) {
                render();
                animate();
            }
        });
    }

}