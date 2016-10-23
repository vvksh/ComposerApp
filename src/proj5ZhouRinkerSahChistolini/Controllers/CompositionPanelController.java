/**
 * File: CompositionPanelController.java
 * @author Victoria Chistolini
 * @author Tiffany Lam
 * @author Joseph Malionek
 * @author Vivek Sah
 * Class: CS361
 * Project: 4
 * Date: October 11, 2016
 */

package proj5ZhouRinkerSahChistolini.Controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import proj5ZhouRinkerSahChistolini.Models.Note;
import proj5ZhouRinkerSahChistolini.Views.GroupRectangle;
import proj5ZhouRinkerSahChistolini.Models.Composition;
import proj5ZhouRinkerSahChistolini.Views.NoteRectangle;
import proj5ZhouRinkerSahChistolini.Views.SelectableRectangle;
import proj5ZhouRinkerSahChistolini.Views.TempoLine;

import java.util.Collection;
import java.util.HashSet;

/**
 * The pane in which all of the notes are stored and displayed.
 */
public class CompositionPanelController {

    /** an ArrayList of NoteRectangles */
    private HashSet<NoteRectangle> noteRectangles;
    private HashSet<SelectableRectangle> rectangles;


    /** a Pane to hold all the notes */
    @FXML
    private Pane compositionPanel;

    /** a Pane to hold all of the lines */
    @FXML
    private Pane staffPane;

    /** the tempoLine */
    @FXML
    private TempoLine tempoLine;

    /** a pointer to the main controller */
    private Controller mainController;

    /** The clickInPaneHandler object */
    private ClickInPanelHandler clickInPanelHandler;

    /** The dragInPaneHandler object */
    private DragInPanelHandler dragInPanelHandler;

    /** The composition object */
    private Composition composition;

    /** a boolean field that keeps track of whether the composition is being played */
    private boolean isPlaying;

    /**
     * Constructs the Panel and draws the appropriate lines.
     */
    @FXML
    public void initialize() {
        this.drawLines();
        rectangles = new HashSet<>();
        noteRectangles = new HashSet<>();
        this.composition = new Composition();
        this.clickInPanelHandler = new ClickInPanelHandler(this);
        this.dragInPanelHandler = new DragInPanelHandler(this.compositionPanel, this);
        this.isPlaying=false;
        this.compositionPanel.toFront();
    }

    /**
     * Initializes the controller with the parent controller
     */
    public void init(Controller controller) {
        this.mainController = controller;
    }

    /**
     * adds the NoteRectangles
     * @param rectangle
     * @param selected
     */
    public void addNoteRectangle(NoteRectangle rectangle, boolean selected){
        this.compositionPanel.getChildren().add(rectangle);
        this.rectangles.add(rectangle);
        this.noteRectangles.add(rectangle);
        if(selected){
            rectangle.setSelected(true);
        }
    }

    /**
     * adds Rectangles
     * @param rectangle
     * @param selected
     */
    public void addRectangle(SelectableRectangle rectangle, boolean selected){
        this.compositionPanel.getChildren().add(rectangle);
        this.rectangles.add(rectangle);
        if(selected){
            rectangle.setSelected(true);
        }
    }

    /**
     * Draws 127 lines with the specified spacing and colors.
     */
    private void drawLines()  {
        for(int i = 1; i < 128; i++)
        {
            Line line = new Line(0, i*10+1, 2000,i*10+1);
            line.setId("lines");
            this.staffPane.getChildren().add(line);
        }
    }

    /**
     * gets the rectangles
     * @return an ArrayList of the rectangles
     */
    public HashSet<SelectableRectangle> getRectangles() {
        return this.rectangles;
    }

    /**
     * gets the selectedRectangles
     * @return an ArrayList of the selected notes
     */
    public HashSet<SelectableRectangle> getSelectedRectangles() {
        HashSet<SelectableRectangle> selectedList = new HashSet<>();
        for(SelectableRectangle rectangle:this.rectangles){
            if(rectangle.isSelected()){
                selectedList.add(rectangle);
            }
        }
        return selectedList;
    }


    public void addNoteToComposition(Note note){
        this.composition.appendNote(note);

    }

    /**
     * Plays the composition and initiates the animation.
     * Stops the current animation and plays a new one if one already exists.
     */
    public void playComposition() {
        this.stopComposition();
        this.isPlaying = true;
        this.composition.buildSong();
//        this.buildSong();

        //only plays when there are rectangles
        if (this.noteRectangles.size() > 0) {
            this.beginAnimation();
            this.composition.play();
        }
    }

    /**
     * Stops and clears the composition and destroys the animation if there is one.
     */
    public void stopComposition() {
        this.isPlaying = false;
        this.stopAnimation();
        this.composition.stop();
    }

    /**
     * Instantiates the line and transition fields and begins the animation based on
     * the length of the composition.
     */
    public void beginAnimation() {
        double maxX = 0;
        for(NoteRectangle rectangle: this.noteRectangles){
            maxX = Math.max(maxX, rectangle.getX() + rectangle.getWidth());
        }
        this.tempoLine.updateTempoLine(maxX);
        this.tempoLine.playAnimation();
    }

    /**
     * Stops the animation and removes the line from the composition panel.
     */
    public void stopAnimation() {
        this.tempoLine.stopAnimation();
    }

    /**
     * clears the selection of the rectangles
     */
    public void clearSelected() {
        for(SelectableRectangle rectangle: this.rectangles){
            if(rectangle.isSelected()){
                rectangle.setSelected(false);
            }
        }
    }

    /**
     * removes the selected rectangles
     */
    public void deleteSelectedNotes() {
        deleteSelected(this.getSelectedRectangles());
    }

    /**
     * removes the selected
     * @param selected list of selected rectangles
     */
    public void deleteSelected(Collection<SelectableRectangle> selected) {
        //first remove from the panel
        for (Rectangle r: selected){
            this.compositionPanel.getChildren().remove(r);
        }
        //then remove from ArrayList of rectangles
        for (SelectableRectangle rect : selected){
            if(rect instanceof NoteRectangle)
                this.composition.removeNote(((NoteRectangle)rect).getNote());
        }
        this.rectangles.removeAll(selected);
        this.noteRectangles.removeAll(selected);



    }



    /**
     * selects all the notes in the composition
     */
    public void selectAllNotes(){
        for (SelectableRectangle rectangle: this.rectangles){
            rectangle.setSelected(true);
        }
    }

    /**
     *
     */
    public void groupSelected(){
        GroupRectangle gesture = new GroupRectangle(this.getSelectedRectangles());
        for (SelectableRectangle rec : gesture.getChildren()){
            rec.setBounded(true);
        }
        DragInNoteHandler handler = new DragInNoteHandler(gesture, this);
        // sets the handlers of these events to be the
        // specified methods in its DragInNoteHandler object
        gesture.setOnMousePressed(handler::handleMousePressed);
        gesture.setOnMouseDragged(handler::handleDragged);
        gesture.setOnMouseReleased(handler::handleMouseReleased);
        gesture.setOnMouseClicked(new ClickInNoteHandler(this));
        addRectangle(gesture, true);
    }

    /**
     *
     */
    public void ungroupSelected(){
        HashSet<GroupRectangle> selectedGroup = new HashSet<>();
        for (SelectableRectangle rec : this.getSelectedRectangles()){
            if (!rec.isBounded() && rec instanceof GroupRectangle){
                selectedGroup.add((GroupRectangle) rec);
            }
        }
        selectedGroup.forEach(GroupRectangle::unbindChildren);
        this.deleteSelected(new HashSet<>(selectedGroup));
    }


    /**
     * Handles mouse click events, extracts x,y coordinates
     * relative to note, gets the name of the instrument, and creates a new
     * note and adds it to the composition and compositionPanel.
     *
     * @param event a mouse click event.
     */
    @FXML
    public void handleMouseClick(MouseEvent event) {
        if (event.isStillSincePress()) { //differentiate from drag and drop
            if (isPlaying) {
                this.stopComposition();
            } else {
                this.clickInPanelHandler.handle(event, this.mainController.getSelectedInstrument());
            }
        }
    }

    /**
     * handles when the mouse is pressed
     */
    @FXML
    public void handleMousePressed(MouseEvent event) {
        this.dragInPanelHandler.handleMousePressed(event);
    }

    /**
     * handles when the mouse is dragged
     */
    @FXML
    public void handleDragged(MouseEvent event) {
        dragInPanelHandler.handleDragged(event);
    }

    /**
     * handles when the mouse is released after dragging
     */
    @FXML
    public void handleDragReleased(MouseEvent event) {
        dragInPanelHandler.handleDragReleased(event);
    }
}
