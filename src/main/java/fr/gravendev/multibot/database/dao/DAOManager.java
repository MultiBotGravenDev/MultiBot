package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;

public class DAOManager {
    private AntiRolesDAO antiRolesDAO;
    private BadWordsDAO badWordsDAO;
    private CustomCommandDAO customCommandDAO;
    private ExperienceDAO experienceDAO;
    private ImmunisedIdDAO immunisedIdDAO;
    private InfractionDAO infractionDAO;
    private LogsDAO logsDAO;
    private QuizMessageDAO quizMessageDAO;
    private RoleDAO roleDAO;
    private VoteDAO voteDAO;
    private WelcomeMessageDAO welcomeMessageDAO;
    private RoleChannelsDAO roleChannelsDAO;

    public DAOManager(DatabaseConnection databaseConnection) {
        antiRolesDAO = new AntiRolesDAO(databaseConnection);
        badWordsDAO = new BadWordsDAO(databaseConnection);
        customCommandDAO = new CustomCommandDAO(databaseConnection);
        experienceDAO = new ExperienceDAO(databaseConnection);
        immunisedIdDAO = new ImmunisedIdDAO(databaseConnection);
        infractionDAO = new InfractionDAO(databaseConnection);
        logsDAO = new LogsDAO(databaseConnection);
        quizMessageDAO = new QuizMessageDAO(databaseConnection);
        roleDAO = new RoleDAO(databaseConnection);
        voteDAO = new VoteDAO(databaseConnection);
        welcomeMessageDAO = new WelcomeMessageDAO(databaseConnection);
        roleChannelsDAO = new RoleChannelsDAO(databaseConnection);
    }

    public AntiRolesDAO getAntiRolesDAO() {
        return antiRolesDAO;
    }

    public BadWordsDAO getBadWordsDAO() {
        return badWordsDAO;
    }

    public CustomCommandDAO getCustomCommandDAO() {
        return customCommandDAO;
    }

    public ExperienceDAO getExperienceDAO() {
        return experienceDAO;
    }

    public ImmunisedIdDAO getImmunisedIdDAO() {
        return immunisedIdDAO;
    }

    public InfractionDAO getInfractionDAO() {
        return infractionDAO;
    }

    public LogsDAO getLogsDAO() {
        return logsDAO;
    }

    public QuizMessageDAO getQuizMessageDAO() {
        return quizMessageDAO;
    }

    public RoleDAO getRoleDAO() {
        return roleDAO;
    }

    public VoteDAO getVoteDAO() {
        return voteDAO;
    }

    public WelcomeMessageDAO getWelcomeMessageDAO() {
        return welcomeMessageDAO;
    }

    public RoleChannelsDAO getRoleChannelsDAO() {
        return roleChannelsDAO;
    }

}
