package com.e3lue.us.model;

/**
 * Created by Leo on 2017/3/23.
 */

public class HttpUrl {
    public static class Url {

        public static final String BASIC = "http://enzatec.wicp.net:8089";
        //public static final String BASIC = "http://192.168.8.8:8089";

        //Login
        public static final String LOGIN = BASIC + "/Controller.aspx?controller=LoggingController&method=Login";
        public static final String Menu = BASIC + "/Controller.aspx?controller=MenuManageController&method=MobileMenu";
        public static final String ManaMenu = BASIC + "/Controller.aspx?controller=MenuManageController&method=MobileManaMenu";
        public static final String Heart = BASIC + "/Controller.aspx?controller=LoggingController&method=Heart";

        //ContactPerson
        public static final String ContactPersonSave = BASIC + "/Controller.aspx?controller=ContactPersonController&method=Save";
        public static final String ContactPersonList = BASIC + "/Controller.aspx?controller=ContactPersonController&method=List";
        public static final String ContactPersonDetail = BASIC + "/Controller.aspx?controller=ContactPersonController&method=Get";
        public static final String ContactPersonCards = BASIC + "/Controller.aspx?controller=ContactPersonController&method=GetNameCard";

        //Upload
        public static final String UPLOADFILE = BASIC + "/Controller.aspx?controller=MyUploaderController&method=Upload";

        //checkin
        public static final String CHECKINGET = BASIC + "/Controller.aspx?controller=CheckInController&method=Get";
        public static final String CHECKINSAVE = BASIC + "/Controller.aspx?controller=CheckInController&method=Save";
        public static final String CHECKINLIST = BASIC + "/Controller.aspx?controller=CheckInController&method=List";
        public static final String GETGPS = BASIC + "/Controller.aspx?controller=CheckInController&method=GetGps";

        //expenses
        public static final String EXPENSESSAVE = BASIC + "/Controller.aspx?controller=ExpensesController&method=Save";
        public static final String ExpensesModify= BASIC + "/Controller.aspx?controller=ExpensesController&method=Modify";
        public static final String ExpensesSubmit= BASIC + "/Controller.aspx?controller=ExpensesController&method=Submit";
        public static final String ExpensesBack= BASIC + "/Controller.aspx?controller=ExpensesController&method=Back";
        public static final String EXPENSESLIST = BASIC + "/Controller.aspx?controller=ExpensesController&method=List";
        public static final String ExpensesGet= BASIC + "/Controller.aspx?controller=ExpensesController&method=Get";
        public static final String EXPENSESLISTForBind = BASIC + "/Controller.aspx?controller=ExpensesController&method=ListForBind";
        public static final String ExpensesListByDiary = BASIC + "/Controller.aspx?controller=ExpensesController&method=ListByDiary";
        public static final String ExpensesUserStatus = BASIC + "/Controller.aspx?controller=ExpensesController&method=UserStatus";

        //diary
        public static final String DiaryGet= BASIC + "/Controller.aspx?controller=DiaryController&method=Get";
        public static final String DIARYSAVE = BASIC + "/Controller.aspx?controller=DiaryController&method=Save";
        public static final String DiaryModify = BASIC + "/Controller.aspx?controller=DiaryController&method=Modify";
        public static final String DiaryList = BASIC + "/Controller.aspx?controller=DiaryController&method=List";
        public static final String DiaryManaList = BASIC + "/Controller.aspx?controller=DiaryController&method=ManaList";
        public static final String DiaryOneDay = BASIC + "/Controller.aspx?controller=DiaryController&method=OneDayList";
        public static final String DiarySubmit = BASIC + "/Controller.aspx?controller=DiaryController&method=Submit";
        public static final String DiaryBack = BASIC + "/Controller.aspx?controller=DiaryController&method=Back";
        public static final String DiaryMessageList = BASIC + "/Controller.aspx?controller=DiaryController&method=MessageList";
        public static final String DiaryMessageSave = BASIC + "/Controller.aspx?controller=DiaryController&method=SaveMessage";

        //company
        public static final String GETSIMPLECOMPANYLIST = BASIC + "/Controller.aspx?controller=CompanyController&method=GetSimpleCompanyList";

        //GameClubOrder
        public static final String GbOrderList = BASIC + "/Controller.aspx?controller=GameClubOrderController&method=OrderList";
        public static final String GbOrderSave = BASIC + "/Controller.aspx?controller=GameClubOrderController&method=SaveOrder";
        public static final String GbOrderDetail = BASIC + "/Controller.aspx?controller=GameClubOrderController&method=GetOrder";
        public static final String GbOoderSubmit = BASIC + "/Controller.aspx?controller=GameClubOrderController&method=SubmitOrder";
        public static final String GbOrderBack = BASIC + "/Controller.aspx?controller=GameClubOrderController&method=BackOrder";
        public static final String GameClub= BASIC + "/Controller.aspx?controller=GameClubOrderController&method=GameClub";

        //GmMailBox
        public static final String GmMailBoxSave = BASIC + "/Controller.aspx?controller=GmMailBoxController&method=Save";

        //Suggestions
        public static final String SuggestionsSave = BASIC + "/Controller.aspx?controller=SuggestionsController&method=Save";
        public static final String SuggestionsList = BASIC + "/Controller.aspx?controller=SuggestionsController&method=List";
        public static final String SuggestionsGet = BASIC + "/Controller.aspx?controller=SuggestionsController&method=Get";

        //FileShare
        //ShareFiless
        public static final String FileShareList = BASIC + "/Controller.aspx?controller=FileShareController&method=List";
        public static final String FileShareLists = BASIC + "/Controller.aspx?controller=FileShareController&method=ShareFiles";
        //workflow
        public static final String WORKTASKLIST = BASIC + "/Controller.aspx?controller=WorkFlowTaskController&method=GetTaskList";
        public static final String WORKFLOWSTEP = BASIC + "/Controller.aspx?controller=WorkFlowTaskController&method=GetFlowSteps";

        //personal
        public static final String LoggingChangePw = BASIC + "/Controller.aspx?controller=LoggingController&method=ChangePassWord";

        //organize
        public static final String OrganizeCharge = BASIC + "/Controller.aspx?controller=OrganizeController&method=GetCharge";

        //Update
        public static final String UPDATE = BASIC + "/apk/update.js";
    }
}
