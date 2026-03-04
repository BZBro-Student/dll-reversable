public class mainMethod {
    public static void main(String[] args) {
        doubleLinkedListReversable<Object> testList = new doubleLinkedListReversable<>();
        testList.add(8);
        testList.add(10);
        testList.add(11);
        testList.add(9);
        System.out.println(testList.toString());
        testList.reverse();
        System.out.println(testList.toString());
    }
}
