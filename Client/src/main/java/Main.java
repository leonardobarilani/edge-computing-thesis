class Main {
    public static void main(String[] args) {

        /**
         * We don't use this client anymore
         * ( https://i.kym-cdn.com/photos/images/original/002/139/758/0c4.jpg )
         */

        StateFulFunction function = new StateFulFunction();
        function.setLog(false);
        try {
            function.init(
                    "admin",
                    "Xa41ubfm4hiq",
                    "172.26.94.11",
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