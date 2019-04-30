package StudyMate;

import java.sql.*;
import java.util.Vector;

public class DataBase {
    StringBuffer sb = new StringBuffer("");
    String jdbcDriver;
    String jdbcUrl;
    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;
    String sql;
    public DataBase() {
        jdbcDriver = "org.mariadb.jdbc.Driver"; //jdbc drive경로
        jdbcUrl = "jdbc:mariadb://localhost:3306/StudyMate"; //db경로
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(jdbcUrl, "root", "root"); //jdbcurl / id / password
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //word - start
    public void insertNewSubject(String NAME) throws SQLException{
        sql = "INSERT INTO Subject Values (0, '" + NAME + "')";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }

    public void insertNewChapter(String NAME, int subjectID) throws SQLException{
        sql = "INSERT INTO Chapter Values (0, '" + NAME + "', NULL ," + subjectID + ")";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }

    public void insertNewWord(String WORD, String MEANING, int chapterID) throws SQLException{
        int vocaID=0;
        pstmt = conn.prepareStatement("select MAX(vocaID) AS ID from vocabulary;");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            vocaID= rs.getInt("ID");
        }
        sql = "INSERT INTO Vocabulary Values ("+(vocaID+1)+", '" + WORD + "', '"+ MEANING +"' ," + chapterID + ")";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }

    public int selectSubjectID(String NAME) throws SQLException{
        sql = "SELECT subjectID from subject where subjectName = '" + NAME + "'";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        String ret = "";
        while(rs.next()){
            ret = rs.getString("subjectID");
        }

        return Integer.parseInt(ret);
    }

    public void updateSubject(String ORIGIN, String NEW) throws SQLException {
        sql = "UPDATE Subject SET subjectName = '" + NEW + "'where subjectName = '" + ORIGIN + "';";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }

    public void updateChapter(String ORIGIN, String NEW, int subjectID) throws SQLException {
        System.out.println("TEST : " + subjectID);
        sql = "UPDATE Chapter SET chapterName = '" + NEW + "'where chapterName = '" + ORIGIN + "' and subjectID = '" + subjectID+"';";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }

    public void updateVoca(String ORIGIN_VOCA, String NEW_VOCA,String ORIGIN_MEAN,String NEW_MEAN,int ChapterID) throws SQLException {
        sql = "UPDATE vocabulary SET voca = '" + NEW_VOCA + "', mean='"+NEW_MEAN+"' where voca = '" + ORIGIN_VOCA + "'and mean='"+ORIGIN_MEAN+"'and chapterID='"+ChapterID+"';";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }
    public void deleteSubject(String NAME) throws SQLException{
        sql = "DELETE FROM subject where subjectName = '" + NAME + "';";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }

    public void deleteChapter(String NAME, int subjectID) throws SQLException{
        sql = "DELETE FROM chapter where chapterName = '" + NAME + "' and subjectID = '" + subjectID+"';";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }

    public void deleteVoca(String NAME, String mean,int chapterID) throws SQLException{
        sql = "DELETE FROM vocabulary where voca = '" + NAME + "' and mean ='" + mean+"'and chapterID='"+chapterID+"';";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
    }


    public int selectChapterID(int subjectID, String CHAPTER) throws SQLException{
        sql = "SELECT chapterID from chapter where chapterName = '" + CHAPTER + "' and subjectID = '" + subjectID+"';'";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        String ret = "";
        while(rs.next()){
            ret = rs.getString("chapterID");
        }

        return Integer.parseInt(ret);
    }

    public Vector<Subject> getSubject() throws SQLException{
        Vector<Subject> Subject = new Vector<Subject>();
        sql = "SELECT * FROM Subject";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        while(rs.next()){
            int subjectId = rs.getInt("subjectID");
            String subjectName = rs.getString("subjectName");
            Subject.add(new Subject(subjectId, subjectName));
        }
        return Subject;
    }

    public Vector<Chapter> getChapter(int ID) throws SQLException{
        Vector<Chapter> chapter = new Vector<Chapter>();
        sql="SELECT * FROM chapter where subjectID='"+ID+"';";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        while(rs.next()){
            int chapterID = rs.getInt("chapterID");
            String chapterName = rs.getString("chapterName");
            String timestamp = rs.getString("timestamp");
            int subjectID = rs.getInt("subjectID");
            chapter.add(new Chapter(chapterID, chapterName, timestamp, subjectID));
        }
        return chapter;

    }

    public Vector<Vocabulary> getVocabulary(String sql) throws SQLException{
        Vector<Vocabulary> vocabulary = new Vector<Vocabulary>();
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        while(rs.next()){
            int vocaID = rs.getInt("vocaID");
            String voca = rs.getString("voca");
            String mean = rs.getString("mean");
            int chapterID = rs.getInt("chapterID");
            vocabulary.add(new Vocabulary(vocaID, voca, mean, chapterID));
        }
        return vocabulary;
    }

    public Vector<Vocabulary> getVocabularyFromChapter(int ChapterID) throws SQLException{
        Vector<Vocabulary> Vocabulary = new Vector<Vocabulary>();
        sql="SELECT * FROM Vocabulary where chapterid = " + ChapterID;
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        while(rs.next()){
            int vocaID = rs.getInt("vocaID");
            String voca = rs.getString("voca");
            String mean = rs.getString("mean");
            int chapterID = rs.getInt("chapterID");
            Vocabulary.add(new Vocabulary(vocaID, voca, mean, chapterID));
        }
        return Vocabulary;
    }

    public Vector<Chapter> getOldChapter() throws SQLException{
        Vector<Chapter> Chapter = new Vector<Chapter>();
        sql="SELECT * FROM chapter order by timestamp";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        while(rs.next()){
            int chapterID = rs.getInt("chapterID");
            String chapterName = rs.getString("chapterName");
            String timestamp = rs.getString("timestamp");
            int subjectID = rs.getInt("subjectID");
            Chapter.add(new Chapter(chapterID, chapterName, timestamp, subjectID));
        }
        return Chapter;
    }
    //word- end
    //schedule database method - start
    public Vector<Dday> getDdayOfSchedule() throws SQLException {
        Vector<Dday> dDay = new Vector<Dday>(1);
        sql = "SELECT TO_DAYS(date) - TO_DAYS(now()) AS dDay, scheduleName FROM( SELECT date,scheduleName FROM schedule WHERE d_day = 'Y')PART where TO_DAYS(date)>TO_DAYS(now()) order by dDay";
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        while (rs.next()) {
            int day = rs.getInt("dDAY");
            String name = rs.getString("scheduleName");
            if (dDay.equals(null)) {
                continue;
            }
            dDay.add(new Dday(day, name));
        }
        return dDay;
    }

    public Vector<Schedule> getSchedule(String sql) throws SQLException {
        Vector<Schedule> schedule = new Vector<Schedule>(0);
        this.sql = sql;
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("scheduleID");
            String scheduleName = rs.getString("scheduleName");
            String date = rs.getString("date");
            String startTime = rs.getString("startTime");
            String endTime = rs.getString("endTime");
            String curState = rs.getString("checkState");
            String comment = rs.getString("comment");
            String d_day = rs.getString("d_day");
            String color = rs.getString("color");
            schedule.add(new Schedule(id, scheduleName, date, startTime, endTime, curState, comment, d_day,color));
        }
        return schedule;
    }

    public void insertSchedule(Schedule schedule) throws SQLException{
        int scheduleID=0;
        pstmt = conn.prepareStatement("select MAX(scheduleID) AS ID from schedule;");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            scheduleID= rs.getInt("ID");
        }
        try {
            String sql = "INSERT INTO schedule VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, scheduleID+1);
            pstmt.setString(2, schedule.scheduleName);
            pstmt.setString(3, schedule.color);
            pstmt.setString(4, schedule.date);
            pstmt.setString(5, schedule.startTime);
            pstmt.setString(6, schedule.endTime);
            pstmt.setString(7, schedule.curState);
            pstmt.setString(8, schedule.content);
            pstmt.setString(9, schedule.d_day);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeSchedule(String scheduleID) {
        try {
            String sql = "DELETE FROM schedule WHERE scheduleID="+scheduleID+";";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBy(String sql) throws SQLException {
        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    //schedule database - end
    //time-table start
    public int removeCourse(Course item) throws SQLException {
        sql = "DELETE FROM course WHERE courseID = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, item.courseID);
        return pstmt.executeUpdate();
    }

    public boolean addCourse(Course item) throws SQLException {
        // SQL INSERT
        sql = "INSERT INTO course (CourseID, credit, credit_theory, credit_practice, courseName, classification, classtime, classroom, school, department, professor)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, item.courseID);
        pstmt.setString(2, item.credit);
        pstmt.setString(3, item.credit_theory);
        pstmt.setString(4, item.credit_practice);
        pstmt.setString(5, item.courseName);
        pstmt.setString(6, item.classification);
        pstmt.setString(7, item.classtime);
        pstmt.setString(8, item.classroom);
        pstmt.setString(9, item.school);
        pstmt.setString(10, item.department);
        pstmt.setString(11, item.professor);

        return pstmt.execute();
    }

    public void updatePath(FilePath path) throws SQLException {
        // SQL INSERT
        sql = "UPDATE file set path= ? where name='"+path.name+"';";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, path.path);
        pstmt.execute();
    }
    public Vector<Course> getCourse() throws SQLException {
        Vector<Course> courseVector = new Vector<Course>();
        sql ="SELECT * FROM course";
        pstmt = conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        while(rs.next()){
            String icourseID = rs.getString("courseID");
            String icredit = rs.getString("credit");
            String icredit_theory = rs.getString("credit_theory");
            String icredit_practice = rs.getString("credit_practice");
            String icourseName = rs.getString("courseName");
            String iclassification = rs.getString("classification");
            String iclasstime = rs.getString("classtime");
            String iclassroom = rs.getString("classroom");
            String ischool = rs.getString("school");
            String idepartment = rs.getString("department");
            String iprofessor = rs.getString("professor");

            courseVector.add(new Course(icourseID, icredit, icredit_theory, icredit_practice, icourseName, iclassification, iclasstime, iclassroom, ischool, idepartment, iprofessor));
        }
        return courseVector;
    }
    //timetable -end
    //filepath - start
    public Vector<FilePath> getFilePath(String sql) throws SQLException {
        Vector<FilePath> file = new Vector<FilePath>(0);
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String path = rs.getString("path");
            String name = rs.getString("name");
            file.add(new FilePath(id,path,name));
        }
        return file;
    }

    public void insertFilePath(FilePath filePath) throws SQLException{
        int id=0;
        pstmt = conn.prepareStatement("select MAX(id) AS ID from file;");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            id= rs.getInt("ID");
        }
        try {
            String sql = "INSERT INTO file VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id+1);
            pstmt.setString(2, filePath.path);
            pstmt.setString(3, filePath.name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //filepath - end

}