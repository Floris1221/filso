package com.ale.filso.views.brewhouse.dialogs;

import com.ale.filso.models.Dictionary.DictionaryCache;
import com.ale.filso.models.Warehouse.DbView.ProductView;
import com.ale.filso.models.Warehouse.ProductService;
import com.ale.filso.views.components.CustomDecimalFormat;
import com.ale.filso.views.components.customDialogs.CustomGridDialog;
import com.ale.filso.views.components.customField.BufferedEntityFiltering;
import com.ale.filso.views.warehouse.filter.WareHouseFilter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static com.ale.filso.APPCONSTANT.PRODUCT_TYPE;


public class ProductDialog extends CustomGridDialog<ProductView> {

    DictionaryCache dictionaryCache;
    ProductService productService;
    WareHouseFilter productFilter = new WareHouseFilter();

    AddIngredientDialog addEditIngredientDialog;
    public ProductDialog(String title, DictionaryCache dictionaryCache, ProductService productService,
                         AddIngredientDialog addEditIngredientDialog){
        super(title, new Grid<>(ProductView.class, false), new ProductView());
        this.dictionaryCache = dictionaryCache;
        this.productService = productService;
        this.addEditIngredientDialog = addEditIngredientDialog;

        createView();
    }

    @Override
    public void createGrid() {

        grid.addColumn(ProductView::getName).setKey("col1")
                .setHeader(getTranslation("models.product.name")).setFlexGrow(1);

        grid.addColumn(ProductView::getProductType).setKey("col2")
                .setHeader(getTranslation("models.product.productType")).setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(item -> {
            CustomDecimalFormat format = new CustomDecimalFormat();
            Span span = new Span(format.format(item.getQuantity()));
            span.setText(span.getText()+" "+item.getUnitOfMeasure());
            return span;
        })).setKey("col3")
                .setComparator(Comparator.comparing(ProductView::getQuantity))
                .setHeader(getTranslation("models.product.quantity")).setFlexGrow(2);

        grid.addColumn(item -> item.getExpirationDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))).setKey("col4")
                .setComparator(Comparator.comparing(ProductView::getExpirationDate))
                .setClassNameGenerator(ProductView::getExpirationColor)
                .setHeader(getTranslation("models.product.expirationDate")).setFlexGrow(1);

        // filtering
        BufferedEntityFiltering filtering = new BufferedEntityFiltering();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(grid.getColumnByKey("col1")).setComponent(
                filtering.createTextFilterHeader(productFilter::setName));
        headerRow.getCell(grid.getColumnByKey("col2")).setComponent(
                filtering.createComboFilterHeaderDictionary(productFilter::setProductType,
                        dictionaryCache.getDict(PRODUCT_TYPE))
        );
    }

    @Override
    public void createActionButton() {
        addEditIngredientDialog.setProduct(selectedEntity); //set choosed product
        addEditIngredientDialog.open();
    }

    @Override
    protected void updateGridDataListWithSearchField() {
        addAttachListener(attachEvent -> { //take new list of product after every open dialog
            productFilter.setDataView(grid.setItems(productService.findAllActivePV(null)));
        });
    }

}
