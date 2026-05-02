package io.fluentmarkdown;

import org.commonmark.node.Node;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;

import java.util.Map;

/**
 * A commonmark {@link AttributeProvider} that injects CSS class strings into
 * rendered HTML elements according to a {@link StyleConfig}.
 *
 * <h2>Merging behaviour</h2>
 * <p>If the renderer already placed a {@code class} attribute on the element
 * (rare but possible with some extensions), the configured classes are
 * <em>appended</em> rather than overwritten, so nothing is silently lost.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * StyleConfig config = Presets.tailwind();
 * HtmlRenderer renderer = HtmlRenderer.builder()
 *     .attributeProviderFactory(StyleAttributeProvider.factory(config))
 *     .build();
 * }</pre>
 */
public final class StyleAttributeProvider implements AttributeProvider {

    private final StyleConfig config;

    private StyleAttributeProvider(StyleConfig config) {
        this.config = config;
    }

    // ── AttributeProvider ──────────────────────────────────────────────────────

    /**
     * Called by commonmark for every HTML element just before it is emitted.
     *
     * @param node       the AST node being rendered
     * @param tagName    the HTML element name (lower-case)
     * @param attributes mutable attribute map — modifications are reflected in output
     */
    @Override
    public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
        String classes = config.classesFor(tagName);
        if (classes == null || classes.isBlank()) {
            return;
        }

        // Append rather than replace so other providers are non-destructive.
        attributes.merge("class", classes, (existing, added) -> existing + " " + added);
    }

    // ── Factory ────────────────────────────────────────────────────────────────

    /**
     * Returns an {@link AttributeProviderFactory} suitable for passing directly
     * to {@link org.commonmark.renderer.html.HtmlRenderer.Builder#attributeProviderFactory}.
     *
     * @param config the style configuration to apply
     * @return a stateless, thread-safe factory
     */
    public static AttributeProviderFactory factory(StyleConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("StyleConfig must not be null");
        }
        // AttributeProviderFactory is a functional interface — lambda is fine.
        return (AttributeProviderContext ctx) -> new StyleAttributeProvider(config);
    }
}
