package edu.iis.mto.staticmock;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.iis.mto.staticmock.reader.NewsReader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigurationLoader.class, NewsReaderFactory.class })
public class NewsLoaderTest 
{
	private ConfigurationLoader configurationLoader;
	private NewsReader newsReader;
	private NewsLoader newsLoader;
	
	@Before
	public void start() 
	{
		configurationLoader = mock(ConfigurationLoader.class);
		when(configurationLoader.loadConfiguration()).thenReturn(new Configuration());
		
		mockStatic(ConfigurationLoader.class);
		when(ConfigurationLoader.getInstance()).thenReturn(configurationLoader);

		newsReader = mock(NewsReader.class);
		when(newsReader.read()).thenReturn(new IncomingNews());

		mockStatic(NewsReaderFactory.class);
		when(NewsReaderFactory.getReader(Mockito.anyString())).thenReturn(newsReader);

		newsLoader = new NewsLoader();
	}
	
	@Test
	public void newsForSubscribersTest() 
	{
        IncomingNews incomingNews = new IncomingNews();
        IncomingInfo incomingInfo = null;
        
        incomingInfo = new IncomingInfo("A type subscription", SubsciptionType.A);
        incomingNews.add(incomingInfo);
        
        incomingInfo = new IncomingInfo("B type subscription", SubsciptionType.C);
        incomingNews.add(incomingInfo);
        
        incomingInfo = new IncomingInfo("None type subscription", SubsciptionType.NONE);
        incomingNews.add(incomingInfo);
        
        when(newsReader.read()).thenReturn(incomingNews);
        
        PublishableNews publishableNews = newsLoader.loadNews();
        assertThat(publishableNews.getSubscribentContent().size(), is(2));
	}
	
	@Test
	public void newsForPublicTest()
	{
		IncomingNews incomingNews = new IncomingNews();
		IncomingInfo incomingInfo = null;
        
        incomingInfo = new IncomingInfo("A type subscription", SubsciptionType.A);
        incomingNews.add(incomingInfo);
        
        incomingInfo = new IncomingInfo("B type subscription", SubsciptionType.C);
        incomingNews.add(incomingInfo);
        
        incomingInfo = new IncomingInfo("None type subscription", SubsciptionType.NONE);
        incomingNews.add(incomingInfo);
        
        when(newsReader.read()).thenReturn(incomingNews);
        
        PublishableNews publishableNews = newsLoader.loadNews();
        assertThat(publishableNews.getPublicContent().size(), is(1));
	}
	
	@Test
	public void dependencyBehaviorTest() 
	{
		newsLoader.loadNews();
		Mockito.verify(configurationLoader, Mockito.times(1)).loadConfiguration();
		Mockito.verify(newsReader, Mockito.times(1)).read();
	}
}
