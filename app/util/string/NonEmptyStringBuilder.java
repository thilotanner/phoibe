package util.string;

public class NonEmptyStringBuilder {
    public static final String DEFAULT_DELIMITER = " ";

    private StringBuilder sb;
    private boolean written = false;
    private String delimiter = DEFAULT_DELIMITER;

    public NonEmptyStringBuilder() {
        sb = new StringBuilder();
    }

    public NonEmptyStringBuilder append(String string) {
        if(string != null && !string.isEmpty()) {
            if(written) {
                sb.append(delimiter);
            }
            sb.append(string);
            written = true;
        }
        return this;
    }

    public NonEmptyStringBuilder addLine() {
        if(written) {
            sb.append("\n");
        }
        written = false;
        return this;
    }

    public NonEmptyStringBuilder setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
