package io.fluentmarkdown;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Fluent entry-point for the FluentMarkdown library.
 *
 * <h2>Quick-start</h2>
 * <pre>{@code
 * // Built-in preset:
 * String html = Markdown.from(text).withTailwind().toHtml();
 *
 * // Bootstrap preset:
 * String html = Markdown.from(text).withBootstrap().toHtml();
 *
 * // Bulma preset:
 * String html = Markdown.from(text).withBulma().toHtml();
 *
 * // Custom config:
 * StyleConfig custom = StyleConfig.builder()
 *     .tag("h1", "my-heading")
 *     .tag("p",  "my-paragraph")
 *     .build();
 * String html = Markdown.from(text).withStyle(custom).toHtml();
 *
 * // No styling (plain HTML):
 * String html = Markdown.from(text).toHtml();
 *
 * // Safe HTML (XSS-unsafe raw HTML in source is stripped):
 * String html = Markdown.from(text).withTailwind().safe().toHtml();
 * }</pre>
 *
 * <h2>Thread-safety</h2>
 * <p>Each call to {@link #from(String)} creates a fresh builder; builders are
 * <em>not</em> thread-safe — do not share a builder instance across threads.
 * The underlying {@link Parser} and {@link HtmlRenderer} are created per
 * {@link #toHtml()} call and are therefore also confined to the calling thread.
 */
public final class Markdown {

    // ── State held by the builder ──────────────────────────────────────────────

    private final String markdownSource;
    private StyleConfig styleConfig;   // null → no styling
    private boolean safeMode = false;  // strip raw HTML from source

    // ── Constructor (private — use factory method) ─────────────────────────────

    private Markdown(String markdownSource) {
        if (markdownSource == null) {
            throw new IllegalArgumentException("markdownSource must not be null");
        }
        this.markdownSource = markdownSource;
    }

    // ── Factory ────────────────────────────────────────────────────────────────

    /**
     * Creates a new builder for the supplied Markdown source text.
     *
     * @param markdownText raw Markdown string; must not be {@code null}
     * @return a new {@link Markdown} builder
     */
    public static Markdown from(String markdownText) {
        return new Markdown(markdownText);
    }

    // ── Preset shortcuts ───────────────────────────────────────────────────────

    /**
     * Applies the Tailwind CSS preset (see {@link Presets#tailwind()}).
     *
     * @return {@code this} for chaining
     */
    public Markdown withTailwind() {
        return withStyle(Presets.tailwind());
    }

    /**
     * Applies the Bootstrap 5 preset (see {@link Presets#bootstrap()}).
     *
     * @return {@code this} for chaining
     */
    public Markdown withBootstrap() {
        return withStyle(Presets.bootstrap());
    }

    /**
     * Applies the Bulma preset (see {@link Presets#bulma()}).
     *
     * @return {@code this} for chaining
     */
    public Markdown withBulma() {
        return withStyle(Presets.bulma());
    }

    /**
     * Applies a fully custom {@link StyleConfig}.
     *
     * <p>Can be called multiple times; each call replaces the previous config.
     *
     * @param config the style configuration to apply; must not be {@code null}
     * @return {@code this} for chaining
     */
    public Markdown withStyle(StyleConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("StyleConfig must not be null");
        }
        this.styleConfig = config;
        return this;
    }

    /**
     * Enables safe (sanitised) output: raw HTML blocks and inline HTML present
     * in the Markdown source are stripped from the rendered output.
     *
     * <p>Use this whenever the Markdown content originates from untrusted user
     * input.
     *
     * @return {@code this} for chaining
     */
    public Markdown safe() {
        this.safeMode = true;
        return this;
    }

    // ── Terminal operation ─────────────────────────────────────────────────────

    /**
     * Parses the Markdown source and renders it to an HTML string.
     *
     * <p>This method is the <em>terminal</em> step of the fluent chain. It may
     * be called multiple times on the same builder; each invocation is
     * independent and produces the same result.
     *
     * @return rendered HTML string
     */
    public String toHtml() {
        // 1. Parse
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownSource);

        // 2. Configure renderer
        HtmlRenderer.Builder rendererBuilder = HtmlRenderer.builder()
                .escapeHtml(safeMode);      // strips raw HTML when safeMode=true

        // 3. Attach style provider only when a config is present
        if (styleConfig != null) {
            rendererBuilder.attributeProviderFactory(
                    StyleAttributeProvider.factory(styleConfig)
            );
        }

        // 4. Render and return
        return rendererBuilder.build().render(document);
    }
}
