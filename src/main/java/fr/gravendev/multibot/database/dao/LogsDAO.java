package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.utils.json.Serializer;
import net.dv8tion.jda.core.entities.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogsDAO extends DAO<Message>{

    public LogsDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(Message message) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO logs(`discord_id`, `message`) VALUES (?, ?)")){

            statement.setString(1, message.getAuthor().getId());
            statement.setString(2, new Serializer<Message>().serialize(message));

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Message get(String value) {
        return null;
    }

    @Override
    public void delete(Message obj) {

    }
}
