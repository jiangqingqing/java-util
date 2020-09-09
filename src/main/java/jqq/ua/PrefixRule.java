package jqq.ua;


class PrefixRule extends AbstractUARule {

	public PrefixRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList) {
		super(Category.PREFIX, os, keyword, maxLength, indexList);
	}

	public PrefixRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList, String spliter) {
		super(Category.PREFIX, os, keyword, maxLength, indexList, spliter, true);
	}

	@Override
	public UserAgent match(String userAgent) {
		String tempUA = userAgent;
		String searchWord = keyword;
		if (super.lower) {
			tempUA = userAgent.toLowerCase();
			searchWord = searchWord.toLowerCase();
		}
		if (tempUA.startsWith(searchWord)) {
			return super.process_inner(userAgent);
		}
		return null;
	}
}