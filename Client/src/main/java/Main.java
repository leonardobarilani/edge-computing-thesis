class Main {
    public static void main(String[] args) {
        StateFulFunction function = new StateFulFunction();
        function.setLog(false);
        try {
            function.init(
                    "admin",
                    "AtwatNsxwnUw",
                    "172.26.92.43",
                    "stateful-append");

            System.out.println(function.call("H"));
            System.out.println(function.call("e"));
            System.out.println(function.call("l"));
            System.out.println(function.call("l"));
            System.out.println(function.call("o"));
            System.out.println(function.call(" "));
            System.out.println(function.call("w"));
            System.out.println(function.call("o"));
            System.out.println(function.call("r"));
            System.out.println(function.call("l"));
            System.out.println(function.call("d"));
            System.out.println(function.call("!"));

            function.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}