package com.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.view.CustomView.ConditionAction.ConditionType.*;

@Route("v1")
public class CustomView extends VerticalLayout {

    private Grid<User> userGrid;
    private Grid<ConditionAction> conditionActionGrid;

    public CustomView() {

        VerticalLayout layout = new VerticalLayout();
        userGrid = new Grid<>();
        userGrid.addColumn(User::getName).setHeader("Имя");
        userGrid.addColumn(User::getAge).setHeader("Возраст");
        userGrid.addColumn(user -> checkState(user).getState).setHeader("Состояние");


        userGrid.addComponentColumn(this::getCheckBox);

        userGrid.setItems(getUsers());

        Component conditionGrid = getConditionGrid();
        layout.add(userGrid, conditionGrid);
        add(layout);
    }

    public Checkbox getCheckBox(User user) {
        Checkbox checkbox = new Checkbox();
        checkbox.addValueChangeListener(event -> {
            if (event.getValue()) {
                conditionActionGrid.setItems(getConditionActions(user));
                conditionActionGrid.setVisible(true);
            } else {
                conditionActionGrid.setVisible(false);
            }
        });

        return checkbox;
    }

    private Component getConditionGrid() {
        VerticalLayout conditionComponent = new VerticalLayout();

        conditionActionGrid = new Grid<>();
        conditionActionGrid.addColumn(conditionAction -> conditionAction.getDescription().getNameCondition()).setHeader("Описание");

        conditionActionGrid.addComponentColumn(ConditionAction::getResult).setHeader("Результат");

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
                case KOVENANT_TAXES:
                    conditionAction.setResult(new Label("<span style='" + redStyle + "'>Проверка пройдена %s</span>".formatted("1")));
                    break;

                case PAYMENT_PACKAGES:
                    conditionAction.setResult(new Label("<span style='" + redStyle + "'>Проверка пройдена %s</span>".formatted("1")));
                    break;

                case ANOTHER_CHECK:
                    conditionAction.setResult(new Label("<span style='" + redStyle + "'>Проверка пройдена %s</span>".formatted("1")));
                    //conditionAction.getHtml().setContentMode(ContentMode.HTML);
                    break;
            }

        });
        dataProvider.refreshAll();
    }

    private List<User> getUsers() {
        return Arrays.asList(new User("John", 18), new User("Alice", 33));
    }


    private List<ConditionAction> getConditionActions(User user) {
        List<ConditionAction> conditionActions = new ArrayList<>();
        if (user.getAge() == 18) {
            conditionActions.add(new ConditionAction(KOVENANT_TAXES, new Label("Проверка ковенантов...")));
            conditionActions.add(new ConditionAction(PAYMENT_PACKAGES, new Label("Проверка Платежей...")));
            conditionActions.add(new ConditionAction(ANOTHER_CHECK, new Label("Остальные проверки")));
        } else {
            conditionActions.add(new ConditionAction(KOVENANT_TAXES, new Label("Остальные проверки")));
        }
        return conditionActions;
    }


    private User.StateType checkState(User user) {
        if (user.name.equals("John")) {
           return User.StateType.UNAVAILABLE;
        }else {
            return User.StateType.DISCONNECT;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private String name;
        private int age;
        private StateType stateType;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Getter
        public enum StateType {
            CONNECT("Подключить"),
            DISCONNECT("Отключить"),
            UNAVAILABLE("Недоступен");

            private final String getState;

            StateType(String getState) {
                this.getState = getState;
            }

            public String getDescription() {
                return getState;
            }

        }

    }

    @Data
    @AllArgsConstructor
    public static class ConditionAction {
        private ConditionType description;
        private Label result;


        @Getter
        public enum ConditionType {
            KOVENANT_TAXES("Ковенанты по налогам"),
            PAYMENT_PACKAGES("Пакеты платежей"),
            ANOTHER_CHECK("Еще одна проверка");

            private final String nameCondition;

            ConditionType(String nameCondition) {
                this.nameCondition = nameCondition;
            }

        }

    }
}
