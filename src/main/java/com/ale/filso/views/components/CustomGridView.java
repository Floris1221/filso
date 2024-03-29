package com.ale.filso.views.components;

import com.ale.filso.seciurity.AuthenticatedUser;
import com.ale.filso.seciurity.UserAuthorization;
import com.ale.filso.views.components.Enums.ButtonType;
import com.ale.filso.views.components.customField.CustomButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;


public abstract class CustomGridView<E> extends CustomView{

    private Div wrapper = new Div();

    @Getter
    protected Grid<E> grid;
    protected HorizontalLayout topButtonsPanel = new HorizontalLayout();
    protected HorizontalLayout tableSearchHl = new HorizontalLayout();

    // selected row - in edit table
    protected E selectedEntity;


    protected CustomGridView(UserAuthorization userAuthorization, Grid<E> grid, E selectedEntity){
        super(userAuthorization);
        this.grid = grid;
        this.selectedEntity = selectedEntity;
    }

    protected void createView(){
        this.setSizeFull();
        add(createGridPageLayout());
        createGrid();
        createButtonsPanel();
        if (grid.getListDataView().getItemCount()>0)                // select first row
            grid.select(grid.getListDataView().getItem(0));
    }

    protected abstract void createButtonsPanel();

    protected abstract void createGrid();


    protected CustomButton addButtonToTablePanel(ButtonType buttonType, boolean hasAccess) {
        CustomButton button;
        if(buttonType == ButtonType.SAVE)
            button = saveButton;
        else if(buttonType == ButtonType.CANCEL)
            button = cancelButton;
        else
            button = new CustomButton(buttonType, hasAccess);
        topButtonsPanel.add(button);
        return button;
    }

    protected TextField createSearchField() {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidth("300px");
        textField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        updateGridDataListWithSearchField(textField.getValue() );
        textField.addValueChangeListener(e -> updateGridDataListWithSearchField(textField.getValue()));

        tableSearchHl.add(new Span(), textField);

        return textField;
    }

    /**
     * metoda do implementacji odświeżania danych w grid dla obiektów, gdzie zawężanie danych
     *
     * @param filterText
     */
    protected void updateGridDataListWithSearchField(String filterText){};

    protected void setResizeableSortableGrid(Integer columnCountResizeable,
                                             Integer columnCountSortable) {

        int columnSize = Math.toIntExact(grid.getColumns().size());
        if (columnCountResizeable!=null && columnCountResizeable<=columnSize)
            columnSize = columnCountResizeable;

        for (int i=0;i<columnSize;i++) {
            grid.getColumns().get(i).setResizable(true);
        }

        columnSize = Math.toIntExact(grid.getColumns().size());
        if (columnCountSortable!=null && columnCountSortable<=columnSize)
            columnSize = columnCountSortable;

        for (int i=0;i<columnSize;i++) {
            grid.getColumns().get(i).setSortable(true);
        }

        grid.setColumnReorderingAllowed(true);

    }


    private Component createGridPageLayout() {

        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        VerticalLayout vl = new VerticalLayout();
        vl.setClassName("p-l flex-grow");


        //odkomentować
        topButtonsPanel.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        topButtonsPanel.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        topButtonsPanel.setSpacing(true);
        topButtonsPanel.add(tableSearchHl);
        tableSearchHl.getStyle().set("margin-right", "auto");

        vl.add(topButtonsPanel);

        vl.add(grid);

        wrapper.setClassName("flex flex-col");
        wrapper.setSizeFull();
        wrapper.add(vl);

        return wrapper;
    }

    protected Icon createBackRoutIcon(){
        Icon backIcon = new Icon("vaadin", "arrow-left");
        backIcon.getStyle().set("font-size","0.8rem");
        backIcon.getStyle().set("align-self","center");
        backIcon.addClickListener(event -> UI.getCurrent().navigate(getBackRoute()));
        backIcon.getElement().setAttribute("title",getTranslation("app.message.back"));
        backIcon.getElement().getThemeList().add("badge primary");
        return backIcon;
    }

    protected String getBackRoute() {
        return  null;
    }
}
