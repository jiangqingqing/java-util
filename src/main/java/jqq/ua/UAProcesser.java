package jqq.ua;



import java.util.List;

class UAProcesser {

	private final NotMatchAnyRule notMatchAnyRule = new NotMatchAnyRule();
	private final List<UARule> preRules;
	private final List<UARule> rules;

	public UAProcesser(List<UARule> preRules, List<UARule> rules) {
		this.preRules = preRules;
		this.rules = rules;
	}

	public UserAgent process(String userAgent) {
		UserAgent ua = null;
		for (UARule rule: preRules) {
			ua = rule.match(userAgent);
			if (ua != null) {
				return ua;
			}
		}

		int leftBracket = userAgent.indexOf("(");
		int rightBracket = userAgent.indexOf(")");
		if (leftBracket < 0 || rightBracket < 0) {
			return notMatchAnyRule.match(userAgent);
		}
		String model = userAgent.substring(leftBracket+1, rightBracket);
		if (userAgent.indexOf(';') < 0) {
			return notMatchAnyRule.match(userAgent);
		}

		for (UARule rule: rules) {
			if (rule.category() == UARule.Category.LINE || rule.category() == UARule.Category.LINE_SIMPLE) {
				ua = rule.match(userAgent);
			} else {
				ua = rule.match(model);
			}
			if (ua != null && !ua.isEmpty()) {
				return ua;
			}
		}
		return notMatchAnyRule.match(userAgent);
	}
}