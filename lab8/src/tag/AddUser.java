package tag;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import entity.User;
import entity.UserList;
import entity.UserList.UserExistsException;
import helper.UserListHelper;

public class AddUser extends SimpleTagSupport {
	// ���� ������ ��� �������� user
	private User user;

	// �����-������ ��� ��������� �������� (���������� �����������)
	public void setUser(User user) {
		this.user = user;
	}

	public void doTag() throws JspException, IOException {
		// ���������� �������� ������ = null (�.�. ������ ���)
		String errorMessage = null;
		// ������� �� ��������� ���������� ����� ������ �������������
		UserList userList = (UserList) getJspContext().getAttribute("users",
				PageContext.APPLICATION_SCOPE);
		// ���������, ��� ����� �� ������
		if (user.getLogin() == null || user.getLogin().equals("")) {
			errorMessage = "����� �� ����� ���� ������!";
		} else {
			// ���������, ��� ��� �� ������
			if (user.getName() == null || user.getName().equals("")) {
				errorMessage = "��� ������������ �� ����� ���� ������!";
			}
		}
		// ���� ������ �� ���� - �������� ������������
		if (errorMessage == null) {
			try {
				// ���������������� ���������� ������������ ������ UserList
				userList.addUser(user);
				// �������� ������?���� ������ ������������� � ����
				UserListHelper.saveUserList(userList);
			} catch (UserExistsException e) {
				// ������ - ������������ � ����� ������� ��� ����������
				errorMessage = "������������ � ����� ������� ��� ����������!";
			}
		}
		// ��������� �������� ������ (����� ��� null) � ������
		getJspContext().setAttribute("errorMessage", errorMessage,
				PageContext.SESSION_SCOPE);
	}
}
