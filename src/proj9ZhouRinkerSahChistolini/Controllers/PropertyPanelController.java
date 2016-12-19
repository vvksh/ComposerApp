/**
 * File: EditMenuController.java
 * @author Victoria Chistolini
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS361
 * Project: 8
 * Date: Dec 13, 2016
 */
package proj9ZhouRinkerSahChistolini.Controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import proj9ZhouRinkerSahChistolini.Controllers.Actions.ChangeVolumeAction;
import proj9ZhouRinkerSahChistolini.Views.GroupRectangle;
import proj9ZhouRinkerSahChistolini.Views.SelectableRectangle;
import java.util.ArrayList;
import java.util.Collection;

/**
 * contorls the property panel
 */
public class PropertyPanelController {

    /* reference to the CompositionPanelController */
    private CompositionPanelController compositionPanelController;

    /* reference to the instrument panel controller */
    private InstrumentPanelController instController;

    @FXML
    /* main Pannel for the property bar */
    private AnchorPane pane;

    @FXML
    /* controls instrument selection box */
    private ComboBox instrumentSelect;

    @FXML
    /* controls pitch selection input */
    private TextField pitchBox;

    @FXML
    /* controls duration selection input */
    private TextField durationBox;

    @FXML
    /* contorls volume input */
    private Slider volumeBar;


    /**
     * Sets up graphic display of the property panel
     */
    public void init(CompositionPanelController compController,
                     InstrumentPanelController instController)
    {
        this.compositionPanelController = compController;
        this.instController = instController;

        this.setPropertyBarVisibility();

        //set up combo box selections
        Collection<String> instrumentOptions = this.instController.getInstrumentNames();
        for (String s : instrumentOptions){
            instrumentSelect.getItems().add(s);
        }

    }

    @FXML
    /**
     * called when the apply button is pused to set instrument properties
     */
    public boolean handleApply(){

        Collection<SelectableRectangle> selectedList =
                this.compositionPanelController.getSelectedRectangles();


        try{
            // set pitch and duration
            double newPitch = Integer.parseInt(this.pitchBox.getCharacters().toString());
            double newDuration = Integer.parseInt(this.durationBox.getCharacters().toString());

            // set instrument
            String selectedInst = (String) this.instrumentSelect.getValue();
            int selectedInstVal = this.instController.getInstrumentValues(selectedInst);


            //set volume
            double volume = (this.volumeBar.getValue()/100)*127;
            this.compositionPanelController.addAction(new ChangeVolumeAction(this.compositionPanelController.getSelectedNotes(),(int) volume));
            this.compositionPanelController.getSelectedNotes().forEach( n -> {
                n.setVolume((int) volume);

            });

            for (SelectableRectangle s : selectedList){
                s.setY(newPitch);
                s.setWidth(newDuration);
                s.setInstrument(selectedInstVal);
            }

        }
        catch (NumberFormatException e){
            return false;
        }

        return true;
    }


    private void setPropertyBarVisibility(){

        BooleanBinding selectedNotesBinding = Bindings.createBooleanBinding(() ->
                        this.compositionPanelController.getSelectedRectangles().size() > 0,
                this.compositionPanelController.getActionController().getUndoActionsProperty()
        );

        this.pane.visibleProperty().bind(selectedNotesBinding);

    }



    public void populatePropertyPanel(){

        Collection<SelectableRectangle> notes =
                this.compositionPanelController.getSelectedRectangles();
        ArrayList<SelectableRectangle> list = new ArrayList<>(notes);

        this.populatePitch(list);
        this.populateDuration(list);



    }


    private void populateDuration(ArrayList<SelectableRectangle> list){

        double commonDuration = list.get(0).getWidth();
        this.durationBox.setDisable(false);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWidth() != commonDuration) {
                this.durationBox.setText("--");
                this.durationBox.setDisable(true);
                break;
            }
        }

        if (!this.durationBox.isDisabled()){
            this.durationBox.setText(Integer.toString((int) commonDuration));
        }

    }


    private void populateInstrument(ArrayList<SelectableRectangle> list){
      //  String commonDuration = list.get(0).getInst;
        this.instrumentSelect.setDisable(false);

        for (int i = 0; i < list.size(); i++) {
        //    if (list.get(i).getWidth() != commonDuration) {
                this.durationBox.setText("--");
                this.durationBox.setDisable(true);
                break;
            }
        }

        //if (!this.durationBox.isDisabled()){
          //  this.durationBox.setText(Integer.toString((int) commonDuration));
       // }




    private void populatePitch(ArrayList<SelectableRectangle> list){

        double commonPitch = list.get(0).getY();
        this.pitchBox.setDisable(false);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getY() != commonPitch) {
                this.pitchBox.setText("--");
                this.pitchBox.setDisable(true);
                break;
            }
        }

        if (!this.pitchBox.isDisabled()){
            this.pitchBox.setText(Integer.toString((int) commonPitch/10));
        }
    }

}
