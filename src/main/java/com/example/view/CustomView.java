package com.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Route("v1")
public class CustomView extends VerticalLayout {

    private Grid<User> userGrid;
    private Grid<ConditionAction> conditionActionGrid;


    public CustomView() {
        VerticalLayout layout = new VerticalLayout();

        userGrid = new Grid<>();
        userGrid.addColumn(User::getName).setHeader("Name");
        userGrid.addColumn(User::getAge).setHeader("Age");

        // Create a column for checkboxes
        userGrid.addComponentColumn(user -> {
            Checkbox checkbox = new Checkbox();
            checkbox.addValueChangeListener(event -> {
                if (checkbox.getValue()) {
                    showConditions(user);
                } else {
                    hideConditions();
                }
            });
            return checkbox;
        }).setHeader("Show Conditions");

        userGrid.setItems(getUsers());


        Component conditionGrid = getConditionGrid();
        layout.add(userGrid, conditionGrid);
        add(layout);
    }

    private Component getConditionGrid() {

        VerticalLayout conditionComponent = new VerticalLayout();

        conditionActionGrid = new Grid<>();
        conditionActionGrid.addColumn(ConditionAction::getDescription).setHeader("Description");
        conditionActionGrid.addColumn(data -> String.valueOf(new Label())).setHeader("Result");

        Button button = new Button("Выполнить проверку");
        button.addClickListener(event -> showResult());
        conditionComponent.add(conditionActionGrid, button);
        return conditionComponent;
    }

    private void showResult() {
        // Получаем доступ к данным в Grid
        DataProvider<ConditionAction, ?> dataProvider = conditionActionGrid.getDataProvider();

        Stream<ConditionAction> fetch = dataProvider.fetch(new Query<>());
        // Проверяем каждое условие и устанавливаем метку соответственно
        fetch.forEach(conditionAction -> {
            if (conditionAction.getDescription().equals("Ковенанты по налогам")) {
                conditionAction.getLabel().setText("Проверка пройдена");
            } else if (conditionAction.getDescription().equals("Пакеты платежей")) {
                conditionAction.getLabel().setText("Проверка пройдена");
            } else if (conditionAction.getDescription().equals("Еще одна проверка")) {
                conditionAction.getLabel().setText("Проверка пройдена");
            }
        });

        // Обновляем данные в Grid
        dataProvider.refreshAll();
    }

    private List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("John", 18));
        users.add(new User("Alice", 33));
        return users;
    }

    private void showConditions(User user) {
        conditionActionGrid.setItems(getConditionActions(user));
        conditionActionGrid.setVisible(true);
    }

    private void hideConditions() {
        conditionActionGrid.setVisible(false);
    }

    private List<ConditionAction> getConditionActions(User user) {
        List<ConditionAction> conditionActions = new ArrayList<>();
        if (user.getAge() == 18) {
            conditionActions.add(new ConditionAction("Ковенанты по налогам", new Label("")));
            conditionActions.add(new ConditionAction("Пакеты платежей", new Label("")));
            conditionActions.add(new ConditionAction("Еще одна проверка", new Label("")));
        } else {
            conditionActions.add(new ConditionAction("Ковенанты по налогам", new Label("")));
        }
        return conditionActions;
    }
    public static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    public static class ConditionAction {
        private String description;
        private Label label;

        public Label getLabel() {
            return label;
        }

        public ConditionAction(String description, Label label) {
            this.description = description;
            this.label = label;
        }

        public ConditionAction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
