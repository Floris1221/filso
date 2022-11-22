package com.ale.filso.views.brewhouse;

import com.ale.filso.models.Brew.Ingredient;
import com.ale.filso.models.Brew.IngredientService;
import com.ale.filso.models.Dictionary.DictionaryCache;
import com.ale.filso.models.User.Role;
import com.ale.filso.models.Warehouse.DbView.ProductView;
import com.ale.filso.models.Warehouse.Product;
import com.ale.filso.models.Warehouse.ProductService;
import com.ale.filso.seciurity.UserAuthorization;
import com.ale.filso.views.brewhouse.dialogs.AddIngredientDialog;
import com.ale.filso.views.brewhouse.dialogs.DeleteIngredientDialog;
import com.ale.filso.views.brewhouse.filter.IngredientFilter;
import com.ale.filso.views.brewhouse.dialogs.ProductDialog;
import com.ale.filso.views.components.CustomGridView;
import com.ale.filso.views.components.Enums.ButtonType;
import com.ale.filso.views.components.customField.BufferedEntityFiltering;
import com.ale.filso.views.components.customField.CustomButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ale.filso.APPCONSTANT.PRODUCT_TYPE;

public class IngredientSearchView extends CustomGridView<Ingredient> {

    ProductDialog dialog;
    AddIngredientDialog addEditDialog;
    DeleteIngredientDialog deleteDialog;
    Binder<Ingredient> binder;
    DictionaryCache dictionaryCache;
    IngredientService ingredientService;
    ProductService productService;
    Ingredient entity;
    Product productEntity;
    IngredientFilter entityFilter = new IngredientFilter();
    BrewDetailsView brewDetailsView;



    protected IngredientSearchView(UserAuthorization userAuthorization, DictionaryCache dictionaryCache,
                                   IngredientService ingredientService, ProductService productService,
                                   BrewDetailsView brewDetailsView) {
        super(userAuthorization, new Grid<>(Ingredient.class, false), new Ingredient());

        this.brewDetailsView = brewDetailsView;
        binder =  new Binder<>(Ingredient.class);
        entity = new Ingredient();
        entity.setBrewId(brewDetailsView.entity.getId());
        productEntity = new Product();

        this.dictionaryCache = dictionaryCache;
        this.ingredientService = ingredientService;
        this.productService = productService;

        createDialog();
        createView();
    }

    @Override
    protected void createGrid() {

        grid.addColumn(item -> item.getProductView().getName()).setKey("col1")
                .setHeader(getTranslation("models.product.name")).setFlexGrow(1);

        grid.addColumn(item -> item.getProductView().getProductType()).setKey("col2")
                .setHeader(getTranslation("models.product.productType")).setFlexGrow(1);

        grid.addColumn(item -> item.getQuantity() + " " + item.getProductView().getUnitOfMeasure()).setKey("col3")
                .setHeader(getTranslation("models.product.quantity")).setFlexGrow(2);

        grid.addColumn(item -> item.getProductView().getExpirationDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))).setKey("col4")
                .setClassNameGenerator(item -> item.getProductView().getExpirationColor())
                .setHeader(getTranslation("models.product.expirationDate")).setFlexGrow(1);

        //Delete button
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, ingredient) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> deleteIngredient(ingredient));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader(getTranslation("ingredientView.grid.delete"));


        // filtering
        BufferedEntityFiltering filtering = new BufferedEntityFiltering();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(grid.getColumnByKey("col1")).setComponent(
                filtering.createTextFilterHeader(entityFilter::setName));
        headerRow.getCell(grid.getColumnByKey("col2")).setComponent(
                filtering.createComboFilterHeaderDictionary(entityFilter::setProductType,
                        dictionaryCache.getDict(PRODUCT_TYPE)));


        setResizeableSortableGrid(null,null);

        createSearchField();
    }

    private void deleteIngredient(Ingredient entity) {
        deleteDialog.setEntity(entity);
        deleteDialog.open();
    }

    @Override
    protected void createButtonsPanel() {
        addButtonToTablePanel(ButtonType.ADD, userAuthorization.hasRole(Role.ADMIN))
                .addClickListener(event -> detailsAction(0));
        addButtonToTablePanel(ButtonType.DETAILS, userAuthorization.hasRole(Role.ADMIN))
                .addClickListener(event -> detailsAction(selectedEntity.getId()));

        grid.addItemDoubleClickListener(event -> detailsAction(selectedEntity.getId()));

        grid.asSingleSelect().addValueChangeListener(event -> {     // grid select action
            if (event.getValue() != null) {
                selectedEntity = event.getValue();
            } else {    // select last selected entity
                grid.select(selectedEntity);
            }
        });
    }

    private void detailsAction(Integer id) {
        if(id == 0) {
            dialog.open();
        }
        else{
            addEditDialog.setEntity(selectedEntity);
            addEditDialog.open();
        }
    }

    @Override
    protected void updateGridDataListWithSearchField(String filterText) {
        super.updateGridDataListWithSearchField(filterText);
        // refresh filter data
        List<Ingredient> ingredients = ingredientService.findAllActive(filterText, brewDetailsView.entity.getId());
        if(!ingredients.isEmpty()){
            List<ProductView> productViews = productService.findAllPVByIds(ingredients.stream().map(Ingredient::getProductId).toList());
            for (Ingredient item: ingredients){
                item.setProductView(productViews.stream().filter(x -> Objects.equals(item.getProductId(), x.getId())).findFirst().orElse(new ProductView()));
            }
        }
        System.out.println(ingredients.stream().map(ingredient -> ingredient.getProductView().getName() + " " + ingredient.getId()).toList());
        entityFilter.setDataView(grid.setItems(ingredients));
    }

    private void createDialog() {
        addEditDialog = new AddIngredientDialog(getTranslation("app.title.product"), grid.getListDataView(),
                ingredientService, brewDetailsView.entity.getId(), productService);
        dialog = new ProductDialog(getTranslation("app.title.product"), dictionaryCache, productService, addEditDialog);
        addEditDialog.setProductGridListDataView(dialog.grid.getListDataView());

        deleteDialog = new DeleteIngredientDialog(getTranslation("ingredientView.dialog.delete.header"),
                grid.getListDataView(), productService, ingredientService, dialog.grid.getListDataView());
    }

}