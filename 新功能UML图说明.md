# 管理员数据看板 — UML建模文档

本次选定的特色功能用例为：**管理员数据看板**。管理员登录后台后，通过点击左侧菜单"数据统计"进入数据看板页面，页面以数字卡片和ECharts图表的形式展示平台运营数据，包含概览数据、订单状态分布饼图、月度订单趋势折线图、热门场馆TOP5柱状图四个模块。

---

## 一、用例图

**PlantUML代码见：diagrams/1-用例图-数据看板.puml**

### 1.1 参与者

本用例只有一个参与者：**管理员**。管理员是系统的后台管理角色（user表中isadmin=1），登录后进入管理后台，拥有查看数据看板的权限。

### 1.2 用例说明

| 用例编号 | 用例名称 | 说明 |
|---------|---------|------|
| UC-1 | 查看数据看板 | 主用例，管理员点击"数据统计"菜单进入统计页面 |
| UC-1.1 | 查看概览数据 | 查看总订单数、总营收、总用户数、总场馆数4个数字指标 |
| UC-1.2 | 查看订单状态分布 | 查看待审核/已通过/已完成/已拒绝4种状态的环形饼图 |
| UC-1.3 | 查看月度订单趋势 | 查看最近6个月订单量变化的折线图 |
| UC-1.4 | 查看热门场馆排行 | 查看订单量TOP5场馆的柱状图 |

### 1.3 关系说明

管理员与"查看数据看板"之间是关联关系，表示管理员直接触发该用例。"查看数据看板"与4个子用例之间是包含关系，表示进入数据看板页面后必然包含这4个数据展示模块。4个子用例之间是并行关系，页面加载时4个AJAX请求同时发出，各自独立渲染。

---

## 二、时序图

**PlantUML代码见：diagrams/2-时序图-数据看板.puml**

### 2.1 参与对象

| 对象名 | 代码名称 | 类型 | 说明 |
|--------|---------|------|------|
| 管理员 | — | 参与者 | 触发查看数据看板操作的用户 |
| 统计页面 | statistics.html | 界面对象 | 前端页面，负责发起AJAX请求和渲染图表 |
| 数据看板控制器 | AdminStatisticsController | 控制对象 | 接收请求、调用DAO、返回JSON |
| 订单数据访问 | OrderDao | 数据对象 | 执行订单相关的统计SQL查询 |
| 用户数据访问 | UserDao | 数据对象 | 执行用户相关的统计SQL查询 |
| 场馆数据访问 | VenueDao | 数据对象 | 执行场馆相关的查询 |
| MySQL数据库 | MySQL | 数据存储 | meethere_db数据库，存储所有业务数据 |

### 2.2 交互流程说明

整个时序图分为4个阶段：

**阶段1：进入统计页面（步骤1-3）**

管理员点击左侧菜单"数据统计"，浏览器发出GET请求到 /statistics_manage。AdminStatisticsController收到请求后，返回一个HTML空壳页面。这个页面只有标题和空的div容器，没有任何数据。

**阶段2：加载概览数据和饼图（步骤4-9）**

statistics.html页面加载完成后，JS自动发出AJAX请求到 /statistics/overview.do。AdminStatisticsController收到请求后，依次调用OrderDao查询订单统计数据（包括订单总数、总营收、4种状态各自的订单数量），调用UserDao查询用户总数，调用VenueDao查询场馆总数。8个数字组装成一个JSON对象返回给前端。前端拿到数据后，用前4个数字渲染4个数字卡片，用后4个数字渲染环形饼图。

**阶段3：加载月度趋势折线图（步骤10-13）**

前端发出AJAX请求到 /statistics/monthlyOrders.do。AdminStatisticsController调用OrderDao的countOrdersByMonth方法，执行按月分组的SQL查询，返回最近6个月的数据。由于数据库返回的是降序（新月份在前），前端拿到后需要倒序遍历，拆成月份数组和数量数组，交给ECharts渲染折线图。

**阶段4：加载热门场馆柱状图（步骤14-18）**

前端发出AJAX请求到 /statistics/venueRank.do。AdminStatisticsController先调用OrderDao的findTopVenues方法，获取订单量TOP5的场馆ID和订单数量。然后对每个场馆ID，调用VenueDao的getOne方法查询场馆名称。这里存在循环调用（5次查数据库），即N+1查询问题。组装完成后返回JSON给前端，前端用场馆名和数量渲染柱状图。

### 2.3 生命周期说明

- **statistics.html**：生命周期最长，从管理员点击菜单开始（步骤1），到页面完整展示结束（步骤19）。在整个过程中一直存在，负责发起请求和接收响应。
- **AdminStatisticsController**：每次AJAX请求时激活，处理完请求返回JSON后立即结束。3个AJAX请求对应3次激活和3次结束。
- **OrderDao / UserDao / VenueDao**：每次方法调用时激活，执行SQL查询返回结果后立即结束。生命周期最短。
- **MySQL**：被动调用，每次SQL执行时激活，返回结果集后立即结束。

### 2.4 循环逻辑说明

步骤15到16之间有一个循环，循环5次。每次根据一个场馆ID调用VenueDao的getOne方法查询场馆名称，再将场馆名和订单数组装成Map对象。这是因为OrderDao的findTopVenues查询只返回场馆ID和订单数，不包含场馆名称，需要额外查询venue表。

---

## 三、类图

**PlantUML代码见：diagrams/3-类图-数据看板.puml**

### 3.1 类说明

| 类名 | 类型 | 说明 |
|------|------|------|
| Order | 实体类 | 订单实体，映射数据库order表。与本用例相关的属性有state（订单状态）、orderTime（下单时间，用于按月统计）、total（订单金额，用于计算总营收） |
| User | 实体类 | 用户实体，映射数据库user表。与本用例相关的属性有id（用于统计总数） |
| Venue | 实体类 | 场馆实体，映射数据库venue表。与本用例相关的属性有venueID（用于关联订单）、venueName（用于柱状图显示） |
| AdminStatisticsController | 控制类 | 数据看板控制器，处理4个HTTP请求，直接注入3个DAO进行数据查询 |
| OrderDao | DAO接口 | 订单数据访问接口，继承JpaRepository，新增5个统计查询方法 |
| UserDao | DAO接口 | 用户数据访问接口，继承JpaRepository，新增1个统计查询方法 |
| VenueDao | DAO接口 | 场馆数据访问接口，继承JpaRepository，使用自带的count和getOne方法 |

### 3.2 类中与本用例相关的属性和方法

**AdminStatisticsController**：

属性：
- orderDao : OrderDao，通过@Autowired注入，用于查询订单统计数据
- userDao : UserDao，通过@Autowired注入，用于查询用户总数
- venueDao : VenueDao，通过@Autowired注入，用于查询场馆总数和场馆名

方法：
- statistics() : String，映射 /statistics_manage，返回统计页面视图
- getOverview() : Map，映射 /statistics/overview.do，返回8个概览数字的JSON
- getMonthlyOrders() : List，映射 /statistics/monthlyOrders.do，返回月度趋势JSON
- getVenueRank() : List，映射 /statistics/venueRank.do，返回场馆排行JSON

**OrderDao**：

方法（均为本次新增）：
- countAllOrders() : int，统计order表总行数
- countByState(int state) : int，统计指定状态的订单数
- sumTotalRevenue() : int，统计已完成订单（state=3）的total字段之和
- countOrdersByMonth() : List，按月分组统计最近6个月的订单数
- findTopVenues() : List，按场馆分组统计订单量取前5名

**UserDao**：

方法（本次新增）：
- countAllUsers() : int，统计user表总行数

**VenueDao**：

方法（已有，非本次新增）：
- count() : int，继承自JpaRepository，统计venue表总行数
- getOne(int id) : Venue，继承自JpaRepository，根据主键查询场馆

### 3.3 类之间关系说明

AdminStatisticsController依赖注入OrderDao、UserDao、VenueDao三个DAO接口，用虚线箭头表示依赖关系。OrderDao操作Order实体（查询order表），UserDao操作User实体（查询user表），VenueDao操作Venue实体（查询venue表）。

### 3.4 设计特点说明

本功能的AdminStatisticsController跳过了Service层，直接调用DAO层。这与其他模块的设计不同，例如OrderController通过OrderService再调用OrderDao。跳过Service层的原因是统计功能只有聚合查询操作，不涉及业务逻辑校验和事务管理，加一层Service只是空壳转发，没有实际意义。

---

## 四、状态图

**PlantUML代码见：diagrams/4-状态图-数据看板页面.puml**

### 4.1 核心对象

本状态图针对的核心对象是**数据看板页面**，描述页面从加载到完整展示的状态变化过程。选择该对象是因为数据看板页面是本功能的唯一载体，其状态变化直接反映了功能的实现过程。页面在加载过程中经历了4个状态，每个状态对应一个数据加载阶段。

### 4.2 状态说明

| 状态 | 说明 |
|------|------|
| 页面空壳 | 初始状态。管理员点击菜单后，浏览器收到HTML空壳页面。此时页面上只有标题"数据统计"，4个数字卡片容器为空，3个图表容器为空。 |
| 加载概览数据 | 前端向 /statistics/overview.do 发送AJAX请求，后端执行8次数据库查询（订单总数、营收、用户数、场馆数、4种状态数量）。查询完成后前端渲染4个数字卡片和饼图。 |
| 加载月度趋势 | 前端向 /statistics/monthlyOrders.do 发送AJAX请求，后端执行按月分组的SQL查询。前端拿到数据后倒序排列，拆分为月份数组和数量数组，渲染折线图。 |
| 加载场馆排行 | 前端向 /statistics/venueRank.do 发送AJAX请求，后端先查TOP5场馆ID和数量，再循环5次查场馆名称。前端拿到数据后渲染柱状图。 |
| 页面完整展示 | 终止状态。4个数字卡片、饼图、折线图、柱状图全部渲染完成，页面所有内容展示完毕。 |

### 4.3 状态转移条件

| 转移 | 触发条件 | 说明 |
|------|---------|------|
| 初始→页面空壳 | 管理员点击"数据统计"菜单 | 浏览器请求 /statistics_manage，后端返回HTML空壳 |
| 页面空壳→加载概览数据 | 页面加载完成，JS自动触发 | 前端自动发起第一个AJAX请求 |
| 加载概览数据→加载月度趋势 | 概览数据返回，卡片和饼图渲染完成 | 前端自动发起第二个AJAX请求 |
| 加载月度趋势→加载场馆排行 | 月度数据返回，折线图渲染完成 | 前端自动发起第三个AJAX请求 |
| 加载场馆排行→页面完整展示 | 场馆数据返回，柱状图渲染完成 | 页面所有内容展示完毕 |

### 4.4 补充说明

3个AJAX请求是并行发出的，页面加载完成后JS同时发起3个请求，不互相等待。但由于浏览器对同一域名有并发限制，实际执行顺序大致是依次完成。状态图展示的是逻辑上的数据依赖顺序：概览数据和饼图依赖 /statistics/overview.do 的返回，折线图依赖 /statistics/monthlyOrders.do 的返回，柱状图依赖 /statistics/venueRank.do 的返回。三个请求互不依赖，哪个先回来就先渲染哪个。
