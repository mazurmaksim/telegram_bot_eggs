package ua.maks.prog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "messages")
public class BotMessages {

    private User user;
    private Admin admin;
    private Common common;

    public static class User {
        private Menu menu;

        public static class Menu {
            private String prompt;
            private String orderCreated;
            private String orderUpdated;
            private String invalidPhone;
            private String askPhone;
            private String orderListEmpty;
            private String orderListTitle;
            private String availableFor;
            private String notAvailableFor;
            private String selectEggsAmount;
            private String myOrders;
            private String doOrder;
            private String newOrder;
            private String getNewOrder;

            private String doneOrder;
            private String orderAmount;

            public String getOrderAmount() {
                return orderAmount;
            }

            public void setOrderAmount(String orderAmount) {
                this.orderAmount = orderAmount;
            }

            public String getDoneOrder() {
                return doneOrder;
            }

            public void setDoneOrder(String doneOrder) {
                this.doneOrder = doneOrder;
            }

            public String getGetNewOrder() {
                return getNewOrder;
            }

            public void setGetNewOrder(String getNewOrder) {
                this.getNewOrder = getNewOrder;
            }

            public String getNewOrder() {
                return newOrder;
            }

            public void setNewOrder(String newOrder) {
                this.newOrder = newOrder;
            }

            public String getDoOrder() {
                return doOrder;
            }

            public void setDoOrder(String doOrder) {
                this.doOrder = doOrder;
            }

            public String getMyOrders() {
                return myOrders;
            }

            public void setMyOrders(String myOrders) {
                this.myOrders = myOrders;
            }

            public String getSelectEggsAmount() {
                return selectEggsAmount;
            }

            public void setSelectEggsAmount(String selectEggsAmount) {
                this.selectEggsAmount = selectEggsAmount;
            }

            public String getNotAvailableFor() {
                return notAvailableFor;
            }

            public void setNotAvailableFor(String notAvailableFor) {
                this.notAvailableFor = notAvailableFor;
            }

            public String getAvailableFor() {
                return availableFor;
            }

            public void setAvailableFor(String availableFor) {
                this.availableFor = availableFor;
            }

            public String getPrompt() {
                return prompt;
            }

            public void setPrompt(String prompt) {
                this.prompt = prompt;
            }

            public String getOrderCreated() {
                return orderCreated;
            }

            public void setOrderCreated(String orderCreated) {
                this.orderCreated = orderCreated;
            }

            public String getOrderUpdated() {
                return orderUpdated;
            }

            public void setOrderUpdated(String orderUpdated) {
                this.orderUpdated = orderUpdated;
            }

            public String getInvalidPhone() {
                return invalidPhone;
            }

            public void setInvalidPhone(String invalidPhone) {
                this.invalidPhone = invalidPhone;
            }

            public String getAskPhone() {
                return askPhone;
            }

            public void setAskPhone(String askPhone) {
                this.askPhone = askPhone;
            }

            public String getOrderListEmpty() {
                return orderListEmpty;
            }

            public void setOrderListEmpty(String orderListEmpty) {
                this.orderListEmpty = orderListEmpty;
            }

            public String getOrderListTitle() {
                return orderListTitle;
            }

            public void setOrderListTitle(String orderListTitle) {
                this.orderListTitle = orderListTitle;
            }
        }

        public Menu getMenu() {
            return menu;
        }

        public void setMenu(Menu menu) {
            this.menu = menu;
        }
    }

    public static class Admin {
        private Menu menu;

        public static class Menu {
            private String prompt;
            private String noOrders;
            private String orderCompleted;
            private String orderAlreadyCompleted;
            private String dataNotSaved;
            private String order;
            private String addToSale;
            private String amountToSale;
            private String addEggs;
            private String enterAmountOfEggs;
            private String telNumSign;
            private String selectCompletedOrder;
            private String notFoundOrder;
            private String alreadyCompleted;
            private String completedOrder;
            private String  yourOrderReady;
            private String today;
            private String yesterday;
            private String months;
            private String showStatisticFrom;
            private String periodStatistic;
            private String statByMonths;
            private String statByWeeks;
            private String airTemperature;
            private String week;
            private String noStatistic;
            private String savedEggsAmount;
            private String added;
            private String addedToSale;
            private String inputCorrectNum;
            private String unknownCommand;
            private String adminCongrats;
            private String averageMonthsAmount;

            public String getAverageMonthsAmount() {
                return averageMonthsAmount;
            }

            public void setAverageMonthsAmount(String averageMonthsAmount) {
                this.averageMonthsAmount = averageMonthsAmount;
            }

            public String getAdminCongrats() {
                return adminCongrats;
            }

            public void setAdminCongrats(String adminCongrats) {
                this.adminCongrats = adminCongrats;
            }

            public String getUnknownCommand() {
                return unknownCommand;
            }

            public void setUnknownCommand(String unknownCommand) {
                this.unknownCommand = unknownCommand;
            }

            public String getInputCorrectNum() {
                return inputCorrectNum;
            }

            public void setInputCorrectNum(String inputCorrectNum) {
                this.inputCorrectNum = inputCorrectNum;
            }

            public String getAddedToSale() {
                return addedToSale;
            }

            public void setAddedToSale(String addedToSale) {
                this.addedToSale = addedToSale;
            }

            public String getAdded() {
                return added;
            }

            public void setAdded(String added) {
                this.added = added;
            }

            public String getSavedEggsAmount() {
                return savedEggsAmount;
            }

            public void setSavedEggsAmount(String savedEggsAmount) {
                this.savedEggsAmount = savedEggsAmount;
            }

            public String getNoStatistic() {
                return noStatistic;
            }

            public void setNoStatistic(String noStatistic) {
                this.noStatistic = noStatistic;
            }

            public String getAirTemperature() {
                return airTemperature;
            }

            public void setAirTemperature(String airTemperature) {
                this.airTemperature = airTemperature;
            }

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public String getStatByWeeks() {
                return statByWeeks;
            }

            public void setStatByWeeks(String statByWeeks) {
                this.statByWeeks = statByWeeks;
            }

            public String getStatByMonths() {
                return statByMonths;
            }

            public void setStatByMonths(String statByMonths) {
                this.statByMonths = statByMonths;
            }

            public String getPeriodStatistic() {
                return periodStatistic;
            }

            public void setPeriodStatistic(String periodStatistic) {
                this.periodStatistic = periodStatistic;
            }

            public String getShowStatisticFrom() {
                return showStatisticFrom;
            }

            public void setShowStatisticFrom(String showStatisticFrom) {
                this.showStatisticFrom = showStatisticFrom;
            }

            public String getYesterday() {
                return yesterday;
            }

            public void setYesterday(String yesterday) {
                this.yesterday = yesterday;
            }

            public String getMonths() {
                return months;
            }

            public void setMonths(String months) {
                this.months = months;
            }

            public String getToday() {
                return today;
            }

            public void setToday(String today) {
                this.today = today;
            }

            public String getYourOrderReady() {
                return yourOrderReady;
            }

            public void setYourOrderReady(String yourOrderReady) {
                this.yourOrderReady = yourOrderReady;
            }

            public String getCompletedOrder() {
                return completedOrder;
            }

            public void setCompletedOrder(String completedOrder) {
                this.completedOrder = completedOrder;
            }

            public String getAlreadyCompleted() {
                return alreadyCompleted;
            }

            public void setAlreadyCompleted(String alreadyCompleted) {
                this.alreadyCompleted = alreadyCompleted;
            }

            public String getNotFoundOrder() {
                return notFoundOrder;
            }

            public void setNotFoundOrder(String notFoundOrder) {
                this.notFoundOrder = notFoundOrder;
            }

            public String getSelectCompletedOrder() {
                return selectCompletedOrder;
            }

            public void setSelectCompletedOrder(String selectCompletedOrder) {
                this.selectCompletedOrder = selectCompletedOrder;
            }

            public String getTelNumSign() {
                return telNumSign;
            }

            public void setTelNumSign(String telNumSign) {
                this.telNumSign = telNumSign;
            }

            public String getEnterAmountOfEggs() {
                return enterAmountOfEggs;
            }

            public void setEnterAmountOfEggs(String enterAmountOfEggs) {
                this.enterAmountOfEggs = enterAmountOfEggs;
            }

            public String getAddEggs() {
                return addEggs;
            }

            public void setAddEggs(String addEggs) {
                this.addEggs = addEggs;
            }

            public String getPrompt() {
                return prompt;
            }

            public void setPrompt(String prompt) {
                this.prompt = prompt;
            }

            public String getNoOrders() {
                return noOrders;
            }

            public void setNoOrders(String noOrders) {
                this.noOrders = noOrders;
            }

            public String getOrderCompleted() {
                return orderCompleted;
            }

            public void setOrderCompleted(String orderCompleted) {
                this.orderCompleted = orderCompleted;
            }

            public String getOrderAlreadyCompleted() {
                return orderAlreadyCompleted;
            }

            public void setOrderAlreadyCompleted(String orderAlreadyCompleted) {
                this.orderAlreadyCompleted = orderAlreadyCompleted;
            }

            public String getDataNotSaved() {
                return dataNotSaved;
            }

            public void setDataNotSaved(String dataNotSaved) {
                this.dataNotSaved = dataNotSaved;
            }

            public String getOrder() {
                return order;
            }

            public void setOrder(String order) {
                this.order = order;
            }

            public String getAddToSale() {
                return addToSale;
            }

            public void setAddToSale(String addToSale) {
                this.addToSale = addToSale;
            }

            public String getAmountToSale() {
                return amountToSale;
            }

            public void setAmountToSale(String amountToSale) {
                this.amountToSale = amountToSale;
            }
        }

        public Menu getMenu() {
            return menu;
        }

        public void setMenu(Menu menu) {
            this.menu = menu;
        }
    }

    public static class Common {
        private String back;
        private String toSale;
        private String eggs;

        public String getEggs() {
            return eggs;
        }

        public void setEggs(String eggs) {
            this.eggs = eggs;
        }

        public String getBack() {
            return back;
        }

        public void setBack(String back) {
            this.back = back;
        }

        public String getToSale() {
            return toSale;
        }

        public void setToSale(String toSale) {
            this.toSale = toSale;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }
}

