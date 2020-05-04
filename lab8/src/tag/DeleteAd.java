package tag;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import entity.Ad;
import entity.AdList;
import entity.User;
import helper.AdListHelper;

public class DeleteAd extends SimpleTagSupport {
	// ���� ������ ��� ��������
	private Ad ad;

	// �����-������ ��� ��������� �������� (���������� �����������)
	public void setAd(Ad ad) {
		this.ad = ad;
	}

	public void doTag() throws JspException, IOException {
		// ���������� �������� ������ = null (�.�. ������ ���)
		String errorMessage = null;
		// ������� �� ��������� ���������� ����� ������ ����������
		AdList adList = (AdList) getJspContext().getAttribute("ads",
				PageContext.APPLICATION_SCOPE);
		// ������� �� ������ �������� �������� ������������
		User currentUser = (User) getJspContext().getAttribute("authUser",
				PageContext.SESSION_SCOPE);
		// ���������, ��� ���������� ���������� ��� �������, � �� �������
		if (currentUser == null
				|| (ad.getId() > 0 && ad.getAuthorId() != currentUser.getId())) {
			// ��������! �����, � �� �����, ������ ����������!
			errorMessage = "�� ��������� �������� ���������, � �������� �� ��������� ������� �������!";
		}
		if (errorMessage == null) {
			// ���������������� �������� ���������� ������ AdList
			adList.deleteAd(ad);
			// �������� ������?���� ������ ���������� � ����
			AdListHelper.saveAdList(adList);
		}
		// ��������� �������� ������ (����� ��� null) � ������
		getJspContext().setAttribute("errorMessage", errorMessage,
				PageContext.SESSION_SCOPE);
	}
}