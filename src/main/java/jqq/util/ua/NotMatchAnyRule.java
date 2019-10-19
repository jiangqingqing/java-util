package jqq.util.ua;


class NotMatchAnyRule implements UARule {

	@Override
	public UserAgent match(String userAgent) {
		return new UserAgent(UserAgent.OS.Unknown, UserAgentAnalyzer.truncateLongUA(userAgent, 64));
	}

	@Override
	public Category category() {
		return Category.NONE;
	}
}