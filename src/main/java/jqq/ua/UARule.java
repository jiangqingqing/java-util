package jqq.ua;

interface UARule {
	static enum Category {
		PREFIX,
		CONTAIN,
		LINE_SIMPLE,
		LINE,
		NONE
	}

	UserAgent match(String userAgent);

	Category category();
}