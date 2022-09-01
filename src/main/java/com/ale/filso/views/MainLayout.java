package com.ale.filso.views;


import com.ale.filso.models.User.Role;
import com.ale.filso.models.User.User;
import com.ale.filso.seciurity.AuthenticatedUser;
import com.ale.filso.views.login.test;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@CssImport(themeFor = "vaadin-grid", value = "./themes/resources/components/grid-color.css")
@CssImport(themeFor = "vaadin-text-field", value = "./themes/resources/components/input-field-color.css")
@CssImport(themeFor = "vaadin-tab", value = "./themes/resources/components/tab-color.css")
@PageTitle("Main")
public class MainLayout extends AppLayout {

    private AuthenticatedUser authenticatedUser;
    private final Tabs menu;
    private H1 viewTitle;

    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        // Use the drawer for the menu
        setPrimarySection(Section.DRAWER);
        // Make the nav bar a header
        addToNavbar(true, createHeaderContent());

//        Span loginIcon = new LineAwesomeIcon("lar la-user icon-10x", "text-3xl");
//        loginIcon.getStyle().set("margin-left", "auto");
//        loginIcon.getStyle().set("padding", "20px");
//        addToNavbar(loginIcon);


        //Optional Avatar


        // Put the menu in the drawer
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }


    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();

        // Configure styling for the header
        layout.setId("header");
        //layout.getThemeList().set("dark", true);
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);

        // Have the drawer toggle button on the left
        layout.add(new DrawerToggle());

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        viewTitle = new H1();
        layout.add(viewTitle);

        Optional<User> isUser = authenticatedUser.get();
        if(isUser.isPresent()){
            User user = isUser.get();

            Avatar avatar = new Avatar(user.getFirstName(), "images/user.png");
            avatar.addClassName("me-xs");

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem(getTranslation("mainLayout.logout"), e -> { authenticatedUser.logout(); });

            Span name = new Span(user.getFirstName());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            HorizontalLayout logoutSpan = new HorizontalLayout();
            VerticalLayout logoutItems = new VerticalLayout(avatar, name);
            logoutSpan.add(logoutItems);
            logoutSpan.getStyle().set("margin-left", "auto");
            logoutSpan.getStyle().set("padding", "20px");

            layout.add(logoutSpan);
        }
        else {
            Anchor loginLink = new Anchor("login", "Sign in");
            addToNavbar(loginLink);
        }


        return layout;
    }


    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        // Configure styling for the drawer
        layout.setSizeFull();
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        // Have a drawer header with an application logo
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new H1("FILSO"));

        //User logo
        Image img = new Image("images/alebrowarlogo.png", "");
        img.setWidth("70px");
        img.setHeight("70px");
        img.addClassName("main-layout-image");

        logoLayout.add(img);


        // Display the logo and the menu in the drawer
        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    private Component[] createMenuItems() {
        return new Tab[]{
                createTab(getTranslation("app.title.brewhouse.menu"), new LineAwesomeIcon("las la-beer", "text-l"), test.class),
                createTab(getTranslation("app.title.fermentationPlant.menu"), new LineAwesomeIcon("las la-percentage", "text-l"), test.class),
                createTab(getTranslation("app.title.bottlingPlant.menu"), new LineAwesomeIcon("las la-wine-bottle", "text-l"), test.class),
                createTab(getTranslation("app.title.warehouse.menu"), new LineAwesomeIcon("las la-boxes", "text-l"), test.class),
                createTab(getTranslation("app.title.office.menu"), new LineAwesomeIcon("lar la-building", "text-l"), test.class),
                createTab(getTranslation("app.title.administration.menu"), new LineAwesomeIcon("las la-user-shield", "text-l"), test.class)};
    }


    private static Tab createTab(String text, Span icon, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(icon);
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        // Select the tab corresponding to currently shown view
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);

        // Set the view title in the header
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }

    @NpmPackage(value = "line-awesome", version = "1.3.0")
    private static class LineAwesomeIcon extends Span {
        public LineAwesomeIcon(String lineawesomeClassnames, String size) {
            // Use Lumo classnames for suitable font size and margin
            addClassNames("me-s", size);
            if (!lineawesomeClassnames.isEmpty()) {
                addClassNames(lineawesomeClassnames);
            }
        }
    }
}

