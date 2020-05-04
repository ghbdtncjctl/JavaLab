package entity;

public class UserList extends ListOfIdentifiables<User> {
	private static final long serialVersionUID = 7115985836992230188L;

	public synchronized User findUser(String login) {
		// »щем пользовател€ с заданным логином
		for (User user : items) {
			if (user.getLogin().equals(login)) {
				return user;
			}
		}
		return null;
	}

	public synchronized User findUser(Integer id) {
		// »щем пользовател€ с заданным идентификатором
		for (User user : items) {
			if (user.getId() == id) {
				return user;
			}
		}
		return null;
	}

	public synchronized User addUser(User user) throws UserExistsException {
		// ≈сли пользователь с данным логином уже зарегистрирован, генерируем
		// исключение
		if (findUser(user.getLogin()) != null)
			throw new UserExistsException();
		// ¬ыбрать следующий незан€тый id дл€ автора
		user.setId(getNextId());
		// ƒобавить автора в список
		items.add(user);
		return user;
	}

	//  ласс искючени€, указывающего при попытке добавлени€ пользовател€,
	// что пользователь с заданным логином уже присутствует в системе
	public static class UserExistsException extends Exception {
		private static final long serialVersionUID = 584737643480913385L;
	}
}
