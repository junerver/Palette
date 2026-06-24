# Expected unsupported / partial coverage

- CommonMark tight vs loose list semantics are not fully modeled.
- Lazy continuation for blockquotes / list paragraphs is not implemented.
- Emphasis delimiter algorithm does not follow the CommonMark spec.
- GFM tables do not model all edge cases from the GFM spec.
- Inline image rendering remains a caller-provided slot; default viewer does not load network resources.

