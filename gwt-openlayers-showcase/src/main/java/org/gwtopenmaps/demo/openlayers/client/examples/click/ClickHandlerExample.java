/**
 *
 * Copyright 2014 sourceforge.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gwtopenmaps.demo.openlayers.client.examples.click;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.HashMap;

import javax.inject.Inject;

import org.gwtopenmaps.demo.openlayers.client.InfoPanel;
import org.gwtopenmaps.demo.openlayers.client.basic.AbstractExample;
import org.gwtopenmaps.demo.openlayers.client.components.store.ShowcaseExampleStore;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.control.Control;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.event.ClickEvent;
import org.gwtopenmaps.openlayers.client.event.ClickListener;
import org.gwtopenmaps.openlayers.client.handler.ClickHandler;
import org.gwtopenmaps.openlayers.client.handler.ClickHandlerOptions;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;

/**
*
* @author Tino Desjardins - SRP
*
*/
public class ClickHandlerExample extends AbstractExample {

    private static final Projection DEFAULT_PROJECTION = new Projection(
            "EPSG:4326");

    private TextArea outputPanel = new TextArea();
    private ListBox listBox = new ListBox();
    private java.util.Map<String, Control> clickControls = new HashMap<String, Control>();
    private String oldValue = null;

    @Inject
    public ClickHandlerExample(ShowcaseExampleStore store) {
        super("Click Handler Example",
                "This example shows the use of the click handler.",
                new String[]{"click", "event", "handler"}, store);
    }

    @Override
    public void buildPanel() {

        // create some MapOptions
        MapOptions defaultMapOptions = new MapOptions();
        defaultMapOptions.setNumZoomLevels(16);

        // create a MapWidget
        MapWidget mapWidget = new MapWidget("500px", "500px", defaultMapOptions);

        contentPanel.add(
                new HTML(
                        "<p>This example shows the use of the click handler.</p>"));
        contentPanel.add(
                new InfoPanel(
                        "<p>Don't forget to add the following line to your HTML if you want to use Google V3. :</p>"
                        + "<p><b>&lt;script src=\"http://maps.google.com/maps/api/js?v=3&amp;sensor=false\"&gt;&lt;/script&gt;</b></p>"));

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setSize("800px", "500px");
        outputPanel.setSize("300px", "475px");
        horizontalPanel.add(mapWidget);

        VerticalPanel eventConsole = new VerticalPanel();

        listBox.setSize("300px", "25px");

        eventConsole.add(listBox);
        eventConsole.add(this.outputPanel);

        horizontalPanel.add(eventConsole);

        contentPanel.add(horizontalPanel);

        // create some Google Layers
        GoogleV3Options gNormalOptions = new GoogleV3Options();
        gNormalOptions.setIsBaseLayer(true);
        gNormalOptions.setType(GoogleV3MapType.G_NORMAL_MAP);
        GoogleV3 gNormal = new GoogleV3("Google Normal", gNormalOptions);

        // and add them to the map
        final Map map = mapWidget.getMap();
        map.addLayer(gNormal);

        // lets add some default controls to the map
        map.addControl(new LayerSwitcher()); //+ sign in the upperright corner to display the layer switcher
        map.addControl(new OverviewMap()); //+ sign in the lowerright to display the overviewmap
        map.addControl(new ScaleLine()); //Display the scaleline

        final String singleKey = "single";
        ClickControl singleClickControl = new ClickControl(this.createClickHandlerOptions(true, false, false, false, -1, singleKey));
        map.addControl(singleClickControl);
        this.clickControls.put(singleKey, singleClickControl);
        listBox.addItem("single only", singleKey);

        final String doubleKey = "double";
        ClickControl doubleClickControl = new ClickControl(this.createClickHandlerOptions(false, true, false, false, -1, doubleKey));
        map.addControl(doubleClickControl);
        this.clickControls.put(doubleKey, doubleClickControl);
        this.listBox.addItem("double only", doubleKey);

        final String bothKey = "both";
        ClickControl bothClickControl = new ClickControl(this.createClickHandlerOptions(true, true, false, false, -1, bothKey));
        map.addControl(bothClickControl);
        this.clickControls.put(bothKey, bothClickControl);
        this.listBox.addItem("both", bothKey);

        // center and zoom to a location
        LonLat lonLat = new LonLat(6.95, 50.94);
        lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(),
                map.getProjection()); //transform lonlat to OSM coordinate system
        map.setCenter(lonLat, 12);

        initWidget(contentPanel);

        mapWidget.getElement().getFirstChildElement().getStyle().setZIndex(0); //force the map to fall behind popups

        this.listBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                activateControl();
            }

        });

        this.activateControl();

    }

    /**
     * Activates the selected control.
     *
     * @param listBox
     */
    private void activateControl() {

        String selectedValue = this.listBox.getValue(this.listBox.getSelectedIndex());

        if(this.oldValue != null) {
            this.clickControls.get(this.oldValue).deactivate();
        }

        Control control = this.clickControls.get(selectedValue);
        control.activate();

        this.oldValue = selectedValue;

    }

    private ClickHandlerOptions createClickHandlerOptions(boolean singleClick, boolean doubleClick, boolean stopSingle, boolean stopDouble, int pixelTolerance, final String mode) {

        ClickHandlerOptions clickHandlerOptions = new ClickHandlerOptions(new ClickListener() {

            @Override
            public void onClick(ClickEvent eventObject) {

                writeOutput(mode + " - single click x=" + String.valueOf(eventObject.getX()) + " y=" +  String.valueOf(eventObject.getY()));

            }

            @Override
            public void onDoubleClick(ClickEvent eventObject) {

                writeOutput(mode + " - double click x=" + String.valueOf(eventObject.getX()) + " y=" +  String.valueOf(eventObject.getY()));

            }

        });

        clickHandlerOptions.setStopSingle(stopSingle);
        clickHandlerOptions.setStopDouble(stopDouble);
        clickHandlerOptions.setSingleClick(singleClick);
        clickHandlerOptions.setDoubleClick(doubleClick);

        return clickHandlerOptions;

    }

    private void writeOutput(String output) {

        boolean lineBreak = this.outputPanel.getText().length() > 0;
        this.outputPanel.setText(this.outputPanel.getText() + ( lineBreak ? "\r\n" : "") + output);

    }

    @Override
    public String getSourceCodeURL() {
        return GWT.getModuleBaseURL() + "examples/click/"
                + "ClickHandlerExample.txt";
    }

    /**
    * Custom control for handling click events.
    *
    * @author Tino Desjardins - SRP
    *
    */
    public class ClickControl extends Control {

        private ClickHandler clickHandler;
        private ClickHandlerOptions clickHandlerOptions;

        public ClickControl(ClickHandlerOptions clickHandlerOptions) {
            this.clickHandlerOptions = clickHandlerOptions;
        }

        @Override
        public boolean activate() {

            if (this.getMap() != null) {

                if (this.clickHandler == null) {
                    this.clickHandler = new ClickHandler(this, this.clickHandlerOptions);
                }

                this.clickHandler.activate();

            }

            return super.activate();
        }

        @Override
        public boolean deactivate() {

            if (this.clickHandler != null) {
                this.clickHandler.deactivate();
            }

            return super.deactivate();
        }

    }

}
