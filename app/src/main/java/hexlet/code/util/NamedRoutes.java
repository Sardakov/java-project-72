package hexlet.code.util;

public class NamedRoutes {

    public static String showPath(Long id) {
        return showPath(String.valueOf(id));
    }

    public static String showPath(String id) {
        return "/urls/" + id;
    }
}
