package archivemanipulator;

public enum SupportedArchive {
    ZIP("zip"),
    TAR("tar")
    ;

    private final String name;

    SupportedArchive(String name) {
        this.name = name;
    }
}
