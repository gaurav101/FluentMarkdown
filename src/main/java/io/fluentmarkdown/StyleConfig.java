package io.fluentmarkdown;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable configuration that maps HTML tag names to CSS class strings.
 *
 * <p>Instances are created via the inner {@link Builder} and are shared freely
 * across threads once constructed.
 *
 * <pre>{@code
 * StyleConfig config = StyleConfig.builder()
 *     .tag("h1", "text-3xl font-bold")
 *     .tag("p",  "mb-4 leading-relaxed")
 *     .build();
 * }</pre>
 */
public final class StyleConfig {

    private final Map<String, String> tagClasses;

    private StyleConfig(Map<String, String> tagClasses) {
        // Defensive copy — caller's builder map is mutable.
        this.tagClasses = Collections.unmodifiableMap(new HashMap<>(tagClasses));
    }

    /**
     * Returns the CSS class string for {@code tag}, or {@code null} if the tag
     * has no mapping in this configuration.
     *
     * @param tag lower-case HTML tag name (e.g. {@code "h1"}, {@code "p"})
     * @return space-separated CSS classes, or {@code null}
     */
    public String classesFor(String tag) {
        return tagClasses.get(tag);
    }

    /** Returns an unmodifiable view of the full tag → classes mapping. */
    public Map<String, String> asMap() {
        return tagClasses;
    }

    /** Creates a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    // ── Builder ────────────────────────────────────────────────────────────────

    /**
     * Mutable accumulator used to construct a {@link StyleConfig}.
     */
    public static final class Builder {

        private final Map<String, String> tagClasses = new HashMap<>();

        private Builder() {}

        /**
         * Maps {@code tag} to the given CSS {@code classes}.
         *
         * <p>Calling this method twice with the same tag replaces the previous
         * mapping.
         *
         * @param tag     lower-case HTML element name
         * @param classes space-separated Tailwind / Bootstrap / arbitrary CSS classes
         * @return {@code this} for chaining
         */
        public Builder tag(String tag, String classes) {
            if (tag == null || tag.isBlank()) {
                throw new IllegalArgumentException("tag must not be null or blank");
            }
            if (classes == null) {
                throw new IllegalArgumentException("classes must not be null");
            }
            tagClasses.put(tag.toLowerCase(), classes);
            return this;
        }

        /**
         * Merges all mappings from {@code other}, overwriting any duplicate tags.
         *
         * @param other another config whose mappings are merged in
         * @return {@code this} for chaining
         */
        public Builder mergeFrom(StyleConfig other) {
            tagClasses.putAll(other.asMap());
            return this;
        }

        /** Builds and returns an immutable {@link StyleConfig}. */
        public StyleConfig build() {
            return new StyleConfig(tagClasses);
        }
    }
}
