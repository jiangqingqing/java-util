package jqq.ua;


class ContainRule extends AbstractUARule {

	public ContainRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList, String spliter) {
		super(Category.CONTAIN, os, keyword, maxLength, indexList, spliter);
	}

	public ContainRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList) {
		super(Category.CONTAIN, os, keyword, maxLength, indexList);
	}

	@Override
	public UserAgent match(String userAgent) {
		String tempUA = userAgent;
		String searchWord = keyword;
		if (super.lower) {
			tempUA = userAgent.toLowerCase();
			searchWord = searchWord.toLowerCase();
		}
		if (tempUA.indexOf(searchWord) < 0) {
			return null;
		}
		return process_inner(userAgent);
	}
}