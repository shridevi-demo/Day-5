
@SprintBoostTest
@RunWith(SpringRunner.class)
public class PaymentPlaybaclListenerTest {

    @MockBean
    EventFlowProcessor eventFlowProcessor;

    @AutoWired //@MockBean
    PaymentPlaybackListener paymentPlaybackListener;

    //@AutoWired
    //ApplicationContext context;

    private String mockTestValue() {
        PaymentInitWrapper payload = new PaymentInitWrapper();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            e.printStackTrace;
        }
        return null;
    }

    @Test
    public void assert_successfull_call_to_eventflowprocessor_requestResponseFlow() {
        String payload = mockTestValues();
        paymentPlaybackListener = context.getBean(PaymentPlaybackListener.class);
        paymentPlaybackListener.listen(payload, EventStage.FWD_FLOW_PARSE_RECOVERABLE_ERROR.toString(), 5
            "EVENT_STORE_PLABACK_MSG", 0);
        verify(eventFlowProcessor, times(1)).requestResponseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).routerFlows(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).responseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void assert_successfull_call_to_eventflowprocessor_routerFlow() {
        String payload = mockTestValues();
        paymentPlaybackListener = context.getBean(PaymentPlaybackListener.class);
        paymentPlaybackListener.listen(payload, EventStage.CLIENT_API_RECOVERABLE_ERROR.toString(), 5
            "EVENT_STORE_PLABACK_MSG", 0);
        verify(eventFlowProcessor, times(1)).routerFlows(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).requestResponseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).responseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void assert_successfull_call_to_eventflowprocessor_responseFlow() {
        String payload = mockTestValues();
        paymentPlaybackListener = context.getBean(PaymentPlaybackListener.class);
        paymentPlaybackListener.listen(payload, EventStage.REST_FLOW_PARSE_RECOVERABLE_ERROR.toString(), 5
            "EVENT_STORE_PLABACK_MSG", 0);
        verify(eventFlowProcessor, times(1)).responseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).requestResponseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).routerFlows(any(PaymentInitWrapper.class));
    }

    @Test
    public void assert_call_with_faulty_key() {
        String payload = mockTestValues();
        paymentPlaybackListener = context.getBean(PaymentPlaybackListener.class);
        paymentPlaybackListener.listen(payload, "SOME_RANDOM_KEY", 5
            "EVENT_STORE_PLABACK_MSG", 0);
        verify(eventFlowProcessor, times(0)).responseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).requestResponseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).routerFlows(any(PaymentInitWrapper.class));
    }

    @Test
    public void assert_call_with_faulty_payload {
        String payload = "faulty_string";
        paymentPlaybackListener = context.getBean(PaymentPlaybackListener.class);
        paymentPlaybackListener.listen(payload, EventStage.REST_FLOW_PARSE_RECOVERABLE_ERROR.toString(), 5
            "EVENT_STORE_PLABACK_MSG", 0);
        verify(eventFlowProcessor, times(0)).responseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).requestResponseFlow(any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).routerFlows(any(PaymentInitWrapper.class));
    }
}