package com.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Html;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Route("v1")
public class CustomView extends VerticalLayout {

    private Grid<User> userGrid;
    private Grid<ConditionAction> conditionActionGrid;

    public CustomView() {
        VerticalLayout layout = new VerticalLayout();

        userGrid = new Grid<>();
        userGrid.addColumn(User::getName).setHeader("Имя");
        userGrid.addColumn(User::getAge).setHeader("Возраст");

        // Создание колонки с чекбоксами
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
        }).setHeader("Показать условия");

        userGrid.setItems(getUsers());

        Component conditionGrid = getConditionGrid();
        layout.add(userGrid, conditionGrid);
        add(layout);
    }

    private Component getConditionGrid() {
        VerticalLayout conditionComponent = new VerticalLayout();

        conditionActionGrid = new Grid<>();
        conditionActionGrid.addColumn(ConditionAction::getDescription).setHeader("Описание");
        conditionActionGrid.addComponentColumn(ConditionAction::getHtml).setHeader("Результат");

        Button button = new Button("Выполнить проверку");
        button.addClickListener(event -> showResult());
        conditionComponent.add(conditionActionGrid, button);
        return conditionComponent;
    }

    private void showResult() {
        DataProvider<ConditionAction, ?> dataProvider = conditionActionGrid.getDataProvider();
        dataProvider.fetch(new Query<>()).forEach(conditionAction -> {
            String redStyle = "color: green;"; // Задаем цвет текста
            String blueStyle = "color: blue;"; // Задаем цвет текста
            String orangeStyle = "color: orange;"; // Задаем цвет текста

            switch (conditionAction.getDescription()) {
                case "Ковенанты по налогам":
                    conditionAction.getHtml().setHtmlContent("<span style='" + redStyle + "'>Проверка пройдена %s</span>".formatted("1") );
                    break;

                case "Пакеты платежей":
                    conditionAction.getHtml().setHtmlContent("<span style='" + blueStyle + "'>Проверка пройдена</span>");
                    break;

                case "Еще одна проверка":
                    conditionAction.getHtml().setHtmlContent("<span style='" + orangeStyle + "'>Проверка пройдена!!!</span>");
                    break;
            }

        });
        dataProvider.refreshAll();
    }

    private List<User> getUsers() {
        return Arrays.asList(new User("John", 18), new User("Alice", 33));
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

            conditionActions.add(new ConditionAction("Ковенанты по налогам", new Html("<span style='\" + \"orange\" + \"'> </span>\"")));
            conditionActions.add(new ConditionAction("Пакеты платежей", new Html("<span style='\" + \"orange\" + \"'> </span>\" ")));
            conditionActions.add(new ConditionAction("Еще одна проверка", new Html("<span style='\" + \"orange\" + \"'> </span>\" ")));
        } else {
            conditionActions.add(new ConditionAction("Ковенанты по налогам", new Html("<span style='\" + \"orange\" + \"'> </span>\" ")));
        }
        return conditionActions;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private String name;
        private int age;
    }

    @Data
    @AllArgsConstructor
    public static class ConditionAction {
        private String description;
        private Html html;
    }
}
