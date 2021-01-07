import dnl.utils.text.table.TextTable;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import ru.oshokin.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;

public class Client {

    Class<?>[] usedClasses;
    OperationType[] usedOperationTypes;
    private Method[] clientMethods;
    private Map<Class<?>, Map<String, Method>> entityFieldsSetters;
    private Map<Class<?>, Map<String, String>> entitySearchableFields;
    private Map<Class<?>, Map<String, String>> entityEditableFields;
    private HashMap<Class<?>, TreeNode<IMenuItem>> entitySearchers;
    private EntityManagerFactory emFactory;
    private Scanner reader;
    private TreeNode<IMenuItem> mainMenu;
    private TreeNode<IMenuItem> rootNode;
    private TreeNode<IMenuItem> previousNode;
    private Object currentEntity;
    private final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");

    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        try {
            emFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        } catch (HibernateException e) {
            System.err.printf("Something went wrong: %s%n", e.getMessage());
            terminate(1);
        }

        initializeMetadata();
        initializeActionsTree();

        reader = new Scanner(System.in);
        processNode(mainMenu, false);
        terminate(0);
    }

    private EntityManager getEntityManager() {
        try {
            return emFactory.createEntityManager();
        } catch (HibernateException e) {
            System.err.printf("Something went wrong: %s%n", e.getMessage());
            terminate(1);
        }
        return null;
    }

    private void initializeMetadata() {
        usedClasses = new Class[]{
                Product.class,
                Customer.class,
                Order.class
        };

        usedOperationTypes = new OperationType[]{
                OperationType.FIND_ENTITY,
                OperationType.ADD_ENTITY,
                OperationType.EDIT_ENTITY,
                OperationType.DELETE_ENTITY,
                OperationType.BACK,
                OperationType.QUIT
        };
        entityFieldsSetters = new LinkedHashMap<>();

        for (Class<?> c: usedClasses) {
            Method[] methods = c.getDeclaredMethods();
            Map<String, Method> lowerCaseMethodNames = new HashMap<>(methods.length);
            for (Method method : methods) {
                lowerCaseMethodNames.put(method.getName().toLowerCase(), method);
            }
            Field[] fields = c.getDeclaredFields();
            Map<String, Method> currentFieldSetters = new LinkedHashMap<>(fields.length);
            for (Field field : fields) {
                Method setter = lowerCaseMethodNames.get("set"+ field.getName().toLowerCase());
                if (setter != null) currentFieldSetters.put(field.getName(), setter);
            }
            entityFieldsSetters.put(c, currentFieldSetters);
        }

        entitySearchableFields = new LinkedHashMap<>(usedClasses.length);
        entityEditableFields = new LinkedHashMap<>(usedClasses.length);

        for (Class<?> c: usedClasses) {
            entitySearchableFields.put(c, getSearchableFields(c));
            entityEditableFields.put(c, getEditableFields(c));
        }
    }

    private void initializeActionsTree() {
        Map<Class<?>, String> Headers = new HashMap<>(3);
        Headers.put(Product.class, "Products");
        Headers.put(Customer.class, "Customers");
        Headers.put(Order.class, "Orders");

        Map<Class<?>, String> Presentations = new HashMap<>(3);
        Presentations.put(Product.class, "product");
        Presentations.put(Customer.class, "customer");
        Presentations.put(Order.class, "order");

        entitySearchers = new HashMap<>(3);
        mainMenu = new TreeNode<>(MenuItem.createMenu(0, ""));

        TreeNode<IMenuItem> currentEntityNode;
        TreeNode<IMenuItem> currentActionNode;
        IMenuItem currentAction;
        String otPattern = null;
        String presentation = null;

        int entityNodeIndex = 1;
        int entityActionNodeIndex = 1;

        for (Class<?> c: usedClasses) {
            currentEntityNode = mainMenu.addChild(new TreeNode<>(MenuItem.createMenu(entityNodeIndex,
                    String.format("%d. %s", entityNodeIndex, Headers.getOrDefault(c, "Que huevada!")))));
            entityActionNodeIndex = 1;

            for (OperationType ot: usedOperationTypes) {
                if (c.equals(Product.class) && ot.equals(OperationType.BACK)) {
                    presentation = String.format("%d. Sales statistics by customer", entityActionNodeIndex, otPattern);
                    currentAction = MenuItem.createAction(entityActionNodeIndex, presentation, OperationType.FORM_REPORT, c, "processProductSalesReport", null);
                    currentActionNode = new TreeNode<>(currentAction);
                    currentEntityNode.addChild(currentActionNode);
                    entityActionNodeIndex++;
                }
                if (c.equals(Customer.class) && ot.equals(OperationType.BACK)) {
                    presentation = String.format("%d. Favorite products", entityActionNodeIndex, otPattern);
                    currentAction = MenuItem.createAction(entityActionNodeIndex, presentation, OperationType.FORM_REPORT, c, "processCustomerSalesReport", null);
                    currentActionNode = new TreeNode<>(currentAction);
                    currentEntityNode.addChild(currentActionNode);
                    entityActionNodeIndex++;
                }
                otPattern = ot.getPattern();
                if (otPattern.contains("%s")) otPattern = String.format(otPattern, Presentations.getOrDefault(c, "Surprise, motherfucker!"));
                presentation = String.format("%d. %s", entityActionNodeIndex, otPattern);

                if (ot.equals(OperationType.FIND_ENTITY)) currentAction = MenuItem.createMenu(entityActionNodeIndex, presentation);
                else currentAction = MenuItem.createAction(entityActionNodeIndex, presentation, ot, c);
                currentActionNode = new TreeNode<>(currentAction);

                if (ot.equals(OperationType.FIND_ENTITY)) entitySearchers.put(c, currentActionNode);
                currentEntityNode.addChild(currentActionNode);
                if (ot.equals(OperationType.FIND_ENTITY)) {

                    int searchEntityNodeIndex = 1;
                    Map<String, String> searchableFields = entitySearchableFields.get(c);

                    for (Map.Entry<String, String> entry : searchableFields.entrySet()) {
                        String fieldName = entry.getKey();
                        String fieldPresentation = entry.getValue();
                        Map<String, Object> parameters = new HashMap<>(1);
                        parameters.put("fieldName", fieldName);
                        currentActionNode.addChild(new TreeNode<>(MenuItem.createAction(searchEntityNodeIndex,
                                String.format("%d. Find by %s", searchEntityNodeIndex, fieldPresentation), ot, c, null, parameters)));
                        searchEntityNodeIndex++;
                    }

                    presentation = String.format("%d. %s", searchEntityNodeIndex, OperationType.BACK.getPattern());
                    currentActionNode.addChild(new TreeNode<>(MenuItem.createAction(searchEntityNodeIndex, presentation, OperationType.BACK, c)));
                    searchEntityNodeIndex++;
                    presentation = String.format("%d. %s", searchEntityNodeIndex, OperationType.QUIT.getPattern());
                    currentActionNode.addChild(new TreeNode<>(MenuItem.createAction(searchEntityNodeIndex, presentation, OperationType.QUIT, c)));
                    searchEntityNodeIndex++;

                } else if (ot.equals(OperationType.EDIT_ENTITY)) {

                    TreeNode<IMenuItem> editEntityNode = new TreeNode<>(MenuItem.createMenu(entityActionNodeIndex, presentation));
                    int editEntityNodeIndex = 1;
                    Map<String, String> editableFields = entityEditableFields.get(c);

                    for (Map.Entry<String, String> entry : editableFields.entrySet()) {
                        String fieldName = entry.getKey();
                        String fieldPresentation = entry.getValue();
                        Map<String, Object> parameters = new HashMap<>(1);
                        parameters.put("fieldName", fieldName);
                        editEntityNode.addChild(new TreeNode<>(MenuItem.createAction(editEntityNodeIndex,
                                String.format("%d. Edit %s", editEntityNodeIndex, fieldPresentation), ot, c, null, parameters)));
                        editEntityNodeIndex++;
                    }

                    presentation = String.format("%d. %s", editEntityNodeIndex, OperationType.DISCARD_CHANGES.getPattern());
                    editEntityNode.addChild(new TreeNode<>(MenuItem.createAction(editEntityNodeIndex, presentation, OperationType.DISCARD_CHANGES, c)));
                    editEntityNodeIndex++;
                    presentation = String.format("%d. %s", editEntityNodeIndex, OperationType.SAVE_CHANGES.getPattern());
                    editEntityNode.addChild(new TreeNode<>(MenuItem.createAction(editEntityNodeIndex, presentation, OperationType.SAVE_CHANGES, c)));
                    editEntityNodeIndex++;
                    presentation = String.format("%d. %s", editEntityNodeIndex, OperationType.QUIT.getPattern());
                    editEntityNode.addChild(new TreeNode<>(MenuItem.createAction(editEntityNodeIndex, presentation, OperationType.QUIT, c)));
                    editEntityNodeIndex++;

                    Map<String, Object> parameters = new HashMap<>(1);
                    parameters.put("editEntityNode", editEntityNode);
                    currentAction.setParameters(parameters);
                }
                entityActionNodeIndex++;
            }
            entityNodeIndex++;
        }
        mainMenu.addChild(new TreeNode<>(MenuItem.createAction(entityNodeIndex, String.format("%d. Quit", entityNodeIndex), OperationType.QUIT, null)));
        clientMethods = this.getClass().getDeclaredMethods();
    }

    private Object processNode(TreeNode<IMenuItem> node, boolean justOnce) {
        Object funcResult = null;
        MenuItem curMenu = (MenuItem) node.getData();
        OperationType currentOperation = curMenu.getOperationType();
        String methodName = curMenu.getMethodName();
        if (currentOperation.equals(OperationType.SHOW_MENU)) {
            funcResult = processShowMenu(node, justOnce);
        } else if (currentOperation.equals(OperationType.BACK)) {
            processBackToParentMenu(node);
        } else if (currentOperation.equals(OperationType.DISCARD_CHANGES)) {
            processDiscardChanges(node);
        } else if (currentOperation.equals(OperationType.SAVE_CHANGES)) {
            processSaveChanges(node);
        } else if (currentOperation.equals(OperationType.QUIT)) {
            processQuit();
        } else if (curMenu.getMethodName() != null) {
            Method invocableMethod = null;
            for (Method currentMethod : clientMethods) {
                String currentMethodName = currentMethod.getName();
                if (currentMethodName.equals(methodName)) {
                    invocableMethod = currentMethod;
                    break;
                }
            }
            if (invocableMethod != null) {
                try {
                    invocableMethod.setAccessible(true);
                    funcResult = invocableMethod.invoke(this, node);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    System.err.printf("You've just fucked up, my friend: %s%n", e.getMessage());
                }
            } else {
                System.err.printf("I coulnd't find method \"%s\". Sorryan!%n", methodName);
            }
        }
        return funcResult;
    }

    private Object processShowMenu(TreeNode<IMenuItem> node, boolean justOnce) {
        Object funcResult = null;
        List<TreeNode<IMenuItem>> nodeChildren = node.getChildren();
        for (TreeNode<IMenuItem> childNode : nodeChildren) {
            System.out.println(childNode.getData().getPresentation());
        }
        TreeNode<IMenuItem> chosenNode;
        while (true) {
            int choice = inputValue(Integer.class);
            chosenNode = findNodeByNumber(node, choice);
            if (chosenNode == null) System.out.println("Wrong option! Wake up, Neo!");
            else break;
        }
        funcResult = processNode(chosenNode, false);
        if (!justOnce) funcResult = processShowMenu(node, false);
        return funcResult;
    }

    private Product processFindProduct(TreeNode<IMenuItem> node) {
        return findCustomEntity(node, Product.class, "Product");
    }

    private Product processAddProduct(TreeNode<IMenuItem> node) {
        return addCustomEntity(node, Product.class, "Product");
    }

    private void processEditProduct(TreeNode<IMenuItem> node) {
        editCustomEntity(node, Product.class, "product");
    }

    private void processDeleteProduct(TreeNode<IMenuItem> node) {
        deleteCustomEntity(Product.class, "product");
    }

    private void processProductSalesReport(TreeNode<IMenuItem> node) {
        Product product;
        String userAnswer;
        while (true) {
            product = inputValue(Product.class);
            if (product != null) break;
            else {
                System.out.println("There's no such product, niggard. Should we try again (Y/N)?");
                userAnswer = inputValue(String.class);
                if (userAnswer.equalsIgnoreCase("Y")) continue;
                else break;
            }
        }
        if (product == null) return;
        EntityManager em = getEntityManager();
        Query query = em.createQuery(
                "SELECT \n" +
                        "    items.order AS order,\n" +
                        "    items.price AS price,\n" +
                        "    items.quantity AS quantity,\n" +
                        "    items.amount AS amount\n" +
                        "FROM\n" +
                        "    OrderItem AS items\n" +
                        "WHERE\n" +
                        "    items.product = :product\n" +
                        "ORDER BY\n" +
                        "     items.id");
        List<Object[]> resultList = query.setParameter("product", product).getResultList();
        em.close();
        int resultListSize = resultList.size();
        Object[][] reportLines = new Object[resultList.size()][];
        for (int i = 0; i < resultListSize; i++) {
            Object[] qr = resultList.get(i);
            Order order = ((Order) qr[0]);
            Customer customer = order.getCustomer();
            reportLines[i] = new Object[] {
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    order.getDate().format(localDateTimeFormatter),
                    qr[1].toString(),
                    qr[2].toString(),
                    qr[3].toString()
            };
        }

        resultList.clear();
        String[] columnNames = {
                "First name",
                "Last name",
                "E-mail",
                "Order date/time",
                "Price",
                "Quantity",
                "Amount"
        };

        TextTable reportTable = new TextTable(columnNames, reportLines);
        reportTable.setAddRowNumbering(true);
        reportTable.setSort(0);
        reportTable.printTable();
        System.out.printf("%n%nALL CHICKS WILL BE YOURS AFTER THEY SEE SUCH A BEAUTY!%n%n");
    }

    private void processCustomerSalesReport(TreeNode<IMenuItem> node) {
        Customer customer;
        String userAnswer;
        while (true) {
            customer = inputValue(Customer.class);
            if (customer != null) break;
            else {
                System.out.println("There's no such customer, niggard. Should we try again (Y/N)?");
                userAnswer = inputValue(String.class);
                if (userAnswer.equalsIgnoreCase("Y")) continue;
                else break;
            }
        }
        if (customer == null) return;
        EntityManager em = getEntityManager();
        Query query = em.createQuery(
                "SELECT\n" +
                        "    items.order AS order,\n" +
                        "    items.product AS product,\n" +
                        "    items.price AS price,\n" +
                        "    items.quantity AS quantity,\n" +
                        "    items.amount AS amount\n" +
                        "FROM\n" +
                        "    OrderItem AS items\n" +
                        "WHERE\n" +
                        "    items.order.customer = :customer\n" +
                        "ORDER BY\n" +
                        "     items.order.date,\n" +
                        "     items.id");
        List<Object[]> resultList = query.setParameter("customer", customer).getResultList();
        em.close();
        int resultListSize = resultList.size();
        Object[][] reportLines = new Object[resultList.size()][];
        for (int i = 0; i < resultListSize; i++) {
            Object[] qr = resultList.get(i);
            Order order = ((Order) qr[0]);
            Product product = ((Product) qr[1]);
            reportLines[i] = new Object[] {
                    order.getDate().format(localDateTimeFormatter),
                    product.getName(),
                    qr[2].toString(),
                    qr[3].toString(),
                    qr[4].toString()
            };
        }

        resultList.clear();
        String[] columnNames = {
                "Order date/time",
                "Product",
                "Price",
                "Quantity",
                "Amount"
        };

        TextTable reportTable = new TextTable(columnNames, reportLines);
        reportTable.setAddRowNumbering(true);
        reportTable.setSort(0);
        reportTable.printTable();
        System.out.printf("%n%nQUE LINDO DE VERDAD! KRASOTISCHA!%n%n");
    }

    private Customer processFindCustomer(TreeNode<IMenuItem> node) {
        return findCustomEntity(node, Customer.class, "customer");
    }

    private Customer processAddCustomer(TreeNode<IMenuItem> node) {
        return addCustomEntity(node, Customer.class, "customer");
    }

    private void processEditCustomer(TreeNode<IMenuItem> node) {
        editCustomEntity(node, Customer.class, "customer");
    }

    private void processDeleteCustomer(TreeNode<IMenuItem> node) {
        deleteCustomEntity(Customer.class, "customer");
    }

    private Order processFindOrder(TreeNode<IMenuItem> node) {
        return findCustomEntity(node, Order.class, "order");
    }

    private Order processAddOrder(TreeNode<IMenuItem> node) {
        return addCustomEntity(node, Order.class, "order");
    }

    private void processEditOrder(TreeNode<IMenuItem> node) {
        editCustomEntity(node, Order.class, "order");
    }

    private void processDeleteOrder(TreeNode<IMenuItem> node) {
        deleteCustomEntity(Order.class, "order");
    }

    private void processBackToParentMenu(TreeNode<IMenuItem> node) {
        TreeNode<IMenuItem> nodeParent;
        if (previousNode != null) {
            nodeParent = previousNode.getParent().getParent();
            previousNode = null;
        }
        else nodeParent = node.getParent().getParent();
        processNode(nodeParent, false);
    }

    private void processDiscardChanges(TreeNode<IMenuItem> node) {
        currentEntity = null;
        if (rootNode != null) {
            TreeNode<IMenuItem> nodeParent = rootNode.getParent().getParent();
            rootNode = null;
            processNode(nodeParent, false);
        }
    }

    private void processSaveChanges(TreeNode<IMenuItem> node) {
        if (currentEntity != null) {
            try {
                EntityManager em = getEntityManager();
                EntityTransaction transaction = em.getTransaction();
                transaction.begin();
                em.merge(currentEntity);
                transaction.commit();
                em.close();
            } catch (RuntimeException e) {
                System.out.printf("We need some magician over here: %s%n", e.getMessage());
            }
            currentEntity = null;
        }
        TreeNode<IMenuItem> nodeParent;
        if (rootNode != null) nodeParent = rootNode.getParent().getParent();
        else nodeParent = node.getParent().getParent();
        rootNode = null;
        processNode(nodeParent, false);
    }

    private void processQuit() {
        System.out.println("Nos vemos, amigo!");
        terminate(0);
    }

    private <T> T findCustomEntity(TreeNode<IMenuItem> node, Class<T> actionEntity, String actionEntityName) {
        previousNode = null;
        rootNode = null;
        IMenuItem menuItem = node.getData();
        Map<String, Object> itemParameters = menuItem.getParameters();
        String searchField = (String) itemParameters.get("fieldName");
        T funcResult = findEntityByFieldInDB(actionEntity, searchField);
        if (funcResult != null) {
            System.out.printf("%s was found: %s%n", actionEntityName, funcResult);
        }
        else System.out.println("Alas, the emperor has no clothes!");
        System.out.printf("Back to previous menu%n%n");
        return funcResult;
    }

    private <T> T addCustomEntity(TreeNode<IMenuItem> node, Class<T> actionEntity, String actionEntityName) {
        previousNode = node;
        rootNode = null;
        T funcResult = addEntityToDB(actionEntity);
        if (funcResult != null) System.out.printf("%s was added: %s%n", actionEntityName, funcResult);
        else System.out.println("Go sell drugs. You aren't true hacker!");
        System.out.printf("Back to previous menu%n%n");
        return funcResult;
    }

    private <T> void editCustomEntity(TreeNode<IMenuItem> node, Class<T> actionEntity, String actionEntityName) {
        previousNode = null;
        rootNode = null;
        IMenuItem menuItem = node.getData();
        Map<String, Object> itemParameters = menuItem.getParameters();
        if (itemParameters.containsKey("editEntityNode")) {
            rootNode = node;
            System.out.printf("Select the %s to edit%n", actionEntityName);
            T entity = inputValue(actionEntity);
            currentEntity = entity;
            if (entity != null) {
                TreeNode<IMenuItem> editEntityNode = (TreeNode<IMenuItem>) itemParameters.get("editEntityNode");
                if (editEntityNode != null) processNode(editEntityNode, false);
            }
        } else if (itemParameters.containsKey("fieldName")) {
            editEntityField(actionEntity, (String) itemParameters.get("fieldName"));
        }
    }

    private <T> void deleteCustomEntity(Class<T> actionEntity, String actionEntityName) {
        previousNode = null;
        rootNode = null;
        System.out.printf("Select the %s to delete%n", actionEntityName);
        T entity = inputValue(actionEntity);
        if (entity != null) {
            System.out.printf("Delete the %s (Y/N)?%n", actionEntityName);
            String userAnswer;
            userAnswer = inputValue(String.class);
            if (userAnswer.equalsIgnoreCase("Y")) {
                try {
                    EntityManager em = getEntityManager();
                    EntityTransaction transaction = em.getTransaction();
                    transaction.begin();
                    em.remove(entity);
                    transaction.commit();
                    em.close();
                } catch (RuntimeException e) {
                    System.out.printf("We need some magician over here: %s%n", e.getMessage());
                }
            }
        }
    }

    private <T> void editEntityField(Class<T> processedClass, String fieldName) {
        Map<String, Method> setters = entityFieldsSetters.get(processedClass);
        List<Field> fields = Arrays.stream(processedClass.getDeclaredFields()).filter(field -> field.getName().equals(fieldName)).collect(Collectors.toList());
        if (fields.size() > 0) {
            Field field = fields.get(0);
            Method setter = setters.get(fieldName);
            if (setter != null) {
                System.out.println("Enter value:");
                Object value = inputValue(field.getType());
                try {
                    setter.invoke(currentEntity, value);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    System.out.printf("Call the Morpheus: %s%n", e.getMessage());
                }
            }
        }
    }

    private <T> T fillEntityFromMap(Class<T> processedClass, Map<String, Object> values) {
        Map<String, Method> setters = entityFieldsSetters.get(processedClass);
        T entity = null;
        try {
            entity = processedClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.printf("Call the Morpheus: %s%n", e.getMessage());
        }
        if (entity != null && setters != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                Method setter = setters.get(entry.getKey());
                try {
                    if (setter != null) setter.invoke(entity, entry.getValue());
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    System.out.printf("Call the Morpheus: %s%n", e.getMessage());
                }
            }
        }
        return entity;
    }

    private <T> T findEntityByFieldInDB(Class<T> processedClass, String fieldName) {
        T funcResult = null;
        List<Field> fields = Arrays.stream(processedClass.getDeclaredFields()).filter(field -> field.getName().equals(fieldName)).collect(Collectors.toList());
        if (fields.size() > 0) {
            Field field = fields.get(0);
            System.out.println("Enter value:");
            Object value = inputValue(field.getType());
            String queryText = String.format("from %s T where T.%s = :value",
                    processedClass.getSimpleName(), fieldName);
            EntityManager em = getEntityManager();
            List<T> resultList = em.createQuery(queryText, processedClass)
                    .setParameter("value", value)
                    .getResultList();
            em.close();
            if (resultList.size() == 1) funcResult = resultList.get(0);
            else if (resultList.size() > 1) {
                System.out.println("Pick your pill, Neo:");
                int index = 1;
                for (T result : resultList) {
                    System.out.printf("%d. %s%n", index++, result.toString());
                }
                int choice;
                while (true) {
                    choice = inputValue(Integer.class);
                    if (choice > resultList.size()) System.out.println("Wrong option! Wake up, Neo!");
                    else break;
                }
                funcResult = resultList.get(choice - 1);
                System.out.println("Good choice, my boy!");
            }
        }
        return funcResult;
    }

    private <T> T addEntityToDB(Class<T> processedClass) {
        T funcResult = null;
        List<Field> fields = Arrays.stream(processedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(EntityField.class) && !field.getAnnotation(EntityField.class).skippedOnInsert())
                .collect(Collectors.toList());
        if (fields.size() > 0) {
            Map<String, Object> values = new LinkedHashMap<>(fields.size());
            for (Field field : fields) {
                EntityField annotation = field.getAnnotation(EntityField.class);
                System.out.printf("Enter \"%s\":%n", annotation.presentation());
                Object value = inputValue(field.getType());
                values.put(field.getName(), value);
            }
            List<OrderItem> orderItems = null;
            if (processedClass.equals(Order.class)) {
                Customer customer = (Customer) values.getOrDefault("customer", null);
                if (customer == null) {
                    System.out.println("One does not simply create order without customer! Que mal es tu nivel de servicio, amigo mio!");
                    return funcResult;
                }
                orderItems = inputOrderItems();
                if (orderItems == null) return funcResult;
            }
            System.out.println("Save changes (Y/N)?");
            String userAnswer = inputValue(String.class);
            if (userAnswer.equalsIgnoreCase("Y")) {
                funcResult = fillEntityFromMap(processedClass, values);
                if (funcResult != null) {
                    if (processedClass.equals(Order.class)) {
                        ((Order) funcResult).setDate(LocalDateTime.now());
                        for (OrderItem orderItem : orderItems) {
                            ((Order) funcResult).addOrderItem(orderItem);
                        }
                    }
                    try {
                        EntityManager em = getEntityManager();
                        EntityTransaction transaction = em.getTransaction();
                        transaction.begin();
                        em.persist(funcResult);
                        transaction.commit();
                        em.close();
                    } catch (RuntimeException e) {
                        System.out.printf("We need some magician over here: %s%n", e.getMessage());
                        funcResult = null;
                    }
                }
            }
        }
        return funcResult;
    }

    private List<OrderItem> inputOrderItems() {
        System.out.println("Now let's enter products:");
        List<OrderItem> orderItems = new ArrayList<>(100);
        OrderItem orderItem = inputOrderItem(orderItems);
        if (orderItem == null) return null;
        return orderItems;
    }

    private OrderItem inputOrderItem(List<OrderItem> orderItems) {
        OrderItem funcResult = null;
        Product product;
        String userAnswer;
        while (true) {
            product = inputValue(Product.class);
            if (product != null) break;
            else {
                System.out.println("There's no such product. Next bukva on the baraban!");
                System.out.println("Should we try again (Y/N)?");
                userAnswer = inputValue(String.class);
                if (userAnswer.equalsIgnoreCase("Y")) continue;
                else break;
            }
        }
        if (product == null) return funcResult;
        else {
            System.out.println("How much veshat' in the pieces?");
            Long quantity = inputValue(Long.class);
            if (quantity == 0) quantity = 1L;
            BigDecimal price;
            price = product.getPrice();
            System.out.printf("Current price: %s. Set new price (Y/N)?%n", price);
            userAnswer = inputValue(String.class);
            if (userAnswer.equalsIgnoreCase("Y")) price = inputValue(BigDecimal.class);
            funcResult = new OrderItem(null, product, price, quantity);
            orderItems.add(funcResult);
            System.out.printf("Add more (Y/N)?%n");
            userAnswer = inputValue(String.class);
            if (userAnswer.equalsIgnoreCase("Y")) {
                System.out.println("Pick up your product, Neo!!!");
                inputOrderItem(orderItems);
            }
        }
        return funcResult;
    }

    private <T> T inputValue(Class<T> inputType) {
        boolean validValueEntered = false;
        T funcResult = null;
        while (!validValueEntered) {
            try {
                //кондово, но для скорости не стал прописывать все типы
                if (inputType.isAssignableFrom(int.class) || inputType.isAssignableFrom(Integer.class)) {
                    funcResult = inputType.cast(reader.nextInt());
                    reader.nextLine();
                    validValueEntered = true;
                }
                else if (inputType.isAssignableFrom(long.class) || inputType.isAssignableFrom(Long.class)) {
                    funcResult = inputType.cast(reader.nextLong());
                    reader.nextLine();
                    validValueEntered = true;
                }
                else if (inputType.isAssignableFrom(BigDecimal.class)) {
                    funcResult = inputType.cast(reader.nextBigDecimal());
                    reader.nextLine();
                    validValueEntered = true;
                }
                else if (inputType.isAssignableFrom(String.class)) {
                    funcResult = inputType.cast(reader.nextLine());
                    validValueEntered = true;
                }
                else if (inputType.isAssignableFrom(LocalDateTime.class)) {
                    System.out.println("Correct date format: dd.MM.yyyy hh:mm:ss. Let's get it on!");
                    reader.findInLine("(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d)");
                    try {
                        MatchResult mr = reader.match();
                        int day = Integer.parseInt(mr.group(1));
                        int month = Integer.parseInt(mr.group(2));
                        int year = Integer.parseInt(mr.group(3));
                        int hour = Integer.parseInt(mr.group(4));
                        int minute = Integer.parseInt(mr.group(5));
                        int second = Integer.parseInt(mr.group(6));

                        funcResult = (T) LocalDateTime.of(year, month, day, hour, minute, second);
                        validValueEntered = true;
                    } catch (IllegalStateException e) {
                        validValueEntered = false;
                        System.err.println("There's no spoon, Neo!");
                    }
                }
                else if (inputType.isAssignableFrom(LocalDate.class)) {
                    System.out.println("Correct date format: dd.MM.yyyy. Let's get it on!");
                    reader.findInLine("(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)");
                    try {
                        MatchResult mr = reader.match();
                        int day = Integer.parseInt(mr.group(1));
                        int month = Integer.parseInt(mr.group(2));
                        int year = Integer.parseInt(mr.group(3));

                        funcResult = (T) LocalDate.of(year, month, day);
                        validValueEntered = true;
                    } catch (IllegalStateException e) {
                        validValueEntered = false;
                        System.err.println("Sorry, Neo. Matrix has reloaded!");
                    }
                }
                else if (entitySearchers.containsKey(inputType))
                {
                    funcResult = inputType.cast(processNode(entitySearchers.get(inputType), true));
                    validValueEntered = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Look, we got another Little Bobby Tables here");
                reader.nextLine();
            }
        }
        return funcResult;
    }

    private Map<String, String> getSearchableFields(Class<?> processedClass) {
        List<Field> fields = Arrays.stream(processedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(EntityField.class) && !field.getAnnotation(EntityField.class).skippedOnSearch())
                .collect(Collectors.toList());
        Map<String, String> funcResult = new LinkedHashMap<>(fields.size());
        for (Field field : fields) {
            funcResult.put(field.getName(), field.getAnnotation(EntityField.class).presentation());
        }
        return funcResult;
    }

    private Map<String, String> getEditableFields(Class<?> processedClass) {
        List<Field> fields = Arrays.stream(processedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(EntityField.class) && !field.getAnnotation(EntityField.class).skippedOnUpdate())
                .collect(Collectors.toList());
        Map<String, String> funcResult = new LinkedHashMap<>(fields.size());
        for (Field field : fields) {
            funcResult.put(field.getName(), field.getAnnotation(EntityField.class).presentation());
        }
        return funcResult;
    }

    private TreeNode<IMenuItem> findNodeByNumber(TreeNode<IMenuItem> node, int number) {
        List<TreeNode<IMenuItem>> nodeChildren = node.getChildren();
        for (TreeNode<IMenuItem> childNode : nodeChildren) {
            if (number == childNode.getData().getRelatedNumber()) return childNode;
        }
        return null;
    }

    private void terminate(int status) {
        if (emFactory != null && emFactory.isOpen()) {
            try {
                emFactory.close();
            } catch (IllegalStateException e) {
            }
        }
        System.exit(status);
    }

}
