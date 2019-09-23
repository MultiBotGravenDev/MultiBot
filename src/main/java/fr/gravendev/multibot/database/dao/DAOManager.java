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

    public DAOManager(DatabaseConnection databaseConnection) {
        this.antiRolesDAO = new AntiRolesDAO(databaseConnection);
        this.badWordsDAO = new BadWordsDAO(databaseConnection);
        this.customCommandDAO = new CustomCommandDAO(databaseConnection);
        this.experienceDAO = new ExperienceDAO(databaseConnection);
        this.immunisedIdDAO = new ImmunisedIdDAO(databaseConnection);
        this.infractionDAO = new InfractionDAO(databaseConnection);
        this.logsDAO = new LogsDAO(databaseConnection);
        this.quizMessageDAO = new QuizMessageDAO(databaseConnection);
        this.roleDAO = new RoleDAO(databaseConnection);
        this.voteDAO = new VoteDAO(databaseConnection);
        this.welcomeMessageDAO = new WelcomeMessageDAO(databaseConnection);
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
}
