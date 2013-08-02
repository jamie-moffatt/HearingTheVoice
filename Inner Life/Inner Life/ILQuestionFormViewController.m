//
//  ILQuestionFormViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 26/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILQuestionFormViewController.h"
#import "common.h"
#import "ILUser.h"
#import "ILAppManager.h"

@interface ILQuestionFormViewController ()
{
    NSArray *sections;
}
@end

@implementation ILQuestionFormViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        data = [ILDataSingleton instance];
        data.currentQuestion = 0;
        data.currentSection = 0;
        
        // TODO: Replace with code based on date registered
        _currentSession = 3;
        sections = [data getQuestionsInSectionsFilteredBySession:_currentSession];
        
        data.responses = [[NSMutableDictionary alloc] init];
        
        NSArray *xs = [data getFlatQuestionArrayBySession:_currentSession];
        for (ILQuestion *q in xs)
        {
            [data.responses setObject:[ILChoice NA] forKey:[NSString stringWithFormat:@"%d", q.questionID]];
        }
    }
    return self;
}

- (void)loadView
{    
    UIView *view = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
    [view setBackgroundColor:[UIColor whiteColor]];
    
    CGFloat width = view.frame.size.width;
    CGFloat height = view.frame.size.height - NAVIGATION_BAR_HEIGHT;
    
    _questionProgressBar = [[ILQuestionProgressBar alloc] initWithFrame:CGRectMake(0, height - 44, width, 44)];
    [_questionProgressBar setBackgroundColor:[UIColor whiteColor]];
    
    UIButton *previousButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [previousButton addTarget:self action:@selector(previousButton:) forControlEvents:UIControlEventTouchUpInside];
    [previousButton setTitle:@"Previous" forState:UIControlStateNormal];
    previousButton.frame = CGRectMake(5, height - _questionProgressBar.frame.size.height - 2 - 44, width/2 - 8, 44);
    
    UIButton *nextButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [nextButton addTarget:self action:@selector(nextButton:) forControlEvents:UIControlEventTouchUpInside];
    [nextButton setTitle:@"Next" forState:UIControlStateNormal];
    nextButton.frame = CGRectMake(previousButton.frame.origin.x + previousButton.frame.size.width + 6,
                                  height - _questionProgressBar.frame.size.height - 2 - 44, width/2 - 8, 44);

    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, width, previousButton.frame.origin.y -5) style:UITableViewStyleGrouped];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    
    [view addSubview:_tableView];
    [view addSubview:previousButton];
    [view addSubview:nextButton];
    [view addSubview:_questionProgressBar];
    self.view = view;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"Questions";
    
    NSMutableArray *sectionLengthSpec = [[NSMutableArray alloc] initWithCapacity:[sections count]];
    for (ILSection *section in sections)
    {
        [sectionLengthSpec addObject:[NSNumber numberWithInteger:[section.questions count]]];
    }
    
    _questionProgressBar.sectionSpecification = sectionLengthSpec;
    _questionProgressBar.currentSection = 0;
    _questionProgressBar.currentSubSection = 0;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)nextButton: (UIButton *)sender
{
    NSInteger numberOfSectionsInCurrentSession = [sections count];
    ILSection *sectionObj = [sections objectAtIndex:data.currentSection];
    NSInteger numberOfQuestionsInCurrentSection = [[data getQuestionsBySection:(sectionObj.sectionID)] count];
    
#pragma mark Submission Code
    
    if (data.currentQuestion == (numberOfQuestionsInCurrentSection - 1) && data.currentSection == (numberOfSectionsInCurrentSession - 1))
    {
        NSLog(@"FINISH");
        
        // Construct Response XML
        NSMutableString *responseXML = [[NSMutableString alloc] init];
        
        ILUser *user = [ILAppManager getUser];
        NSDate *now = [NSDate date];
        NSDateFormatter *df = [[NSDateFormatter alloc] init];
        [df setLocale: [[NSLocale alloc] initWithLocaleIdentifier:@"en_GB"]];
        [df setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        NSString *date = [df stringFromDate:now];
        
        [responseXML appendFormat:@"<submission userID=\"%d\" sessionID=\"%d\" notificationTime=\"%@\" submissionTime=\"%@\">", user.userID, _currentSession, date, date];
        for (NSString *key in data.responses)
        {
            NSString *questionID = key;
            ILChoice *chosenChoiceObject = [data.responses objectForKey:key];
            if (chosenChoiceObject)
            {
                [responseXML appendFormat:@"<response questionID=\"%@\" response=\"%@\" />", questionID, chosenChoiceObject.value];
            }
            else
            {
                [responseXML appendFormat:@"<response questionID=\"%@\" response=\"N/A\" />", questionID];
            }
        }
        [responseXML appendString:@"</submission>"];
        
        // TODO: Add error checking
        
        NSFileManager* fm = [[NSFileManager alloc] init];
        NSURL* docsurl = [fm URLForDirectory:NSDocumentDirectory
                                    inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:nil];
        NSURL* myfolder = [docsurl URLByAppendingPathComponent:@"ILSubmissions"];
        BOOL ok = [fm createDirectoryAtURL:myfolder
               withIntermediateDirectories:YES attributes:nil error:nil];
        
        NSError* error = nil;
        BOOL writeSuccess = [responseXML writeToURL:[myfolder URLByAppendingPathComponent:[NSString stringWithFormat:@"submission%d.xml", _currentSession]] atomically:YES encoding:NSUTF8StringEncoding error:&error];
        
        if (writeSuccess)
        {        
            NSMutableDictionary *completed = [NSMutableDictionary dictionaryWithDictionary:[ILAppManager getSessionsCompleted]];
            [completed setObject:[NSNumber numberWithBool:YES] forKey:[NSString stringWithFormat:@"%d", _currentSession]];
            [ILAppManager setSessionsCompleted:completed];

        }
        
        NSLog(@"Submitting ResponseXML: %@", responseXML);
        
        NSURL *responseAPI_URL = [NSURL URLWithString:RESPONSE_API_ENDPOINT];
        NSMutableURLRequest *URL_Request = [NSMutableURLRequest requestWithURL:responseAPI_URL];
        [URL_Request setHTTPMethod:@"POST"];
        [URL_Request setValue:@"text/xml" forHTTPHeaderField:@"Content-Type"];
        [URL_Request setHTTPBody:[responseXML dataUsingEncoding:NSUTF8StringEncoding]];
        
        [NSURLConnection sendAsynchronousRequest:URL_Request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *body, NSError *error)
         {
             if (body)
             {
                 NSString *s = [[NSString alloc] initWithData:body encoding:NSUTF8StringEncoding];
                 NSLog(@"Web API Response:\n%@", s);
                 
                 int scode = [((NSHTTPURLResponse *)response) statusCode];
                 
                 // HTTP (201 Created).
                 if (scode == 201)
                 {
                     NSMutableDictionary *submitted = [NSMutableDictionary dictionaryWithDictionary:[ILAppManager getSessionsSubmitted]];
                     [submitted setObject:[NSNumber numberWithBool:YES] forKey:[NSString stringWithFormat:@"%d", _currentSession]];
                     [ILAppManager setSessionsSubmitted:submitted];
                 }
                 
                 [self.navigationController popViewControllerAnimated:YES];
             }
         }];
        return;
    }
    
    if (data.currentQuestion == (numberOfQuestionsInCurrentSection - 1))
    {
        data.currentSection++;
        _questionProgressBar.currentSection++;
        data.currentQuestion = 0;
        _questionProgressBar.currentSubSection = 0;
    }
    else
    {
        data.currentQuestion++;
        _questionProgressBar.currentSubSection++;
    }
    
    NSLog(@"Section Index: %d", data.currentSection);
    NSLog(@"Question Index: %d", data.currentQuestion);
    
    [self.tableView reloadData];
}

- (void)previousButton: (UIButton *)sender
{   
    if (data.currentSection == 0 && data.currentQuestion == 0)
    {
        return;
    }
    
    if (data.currentQuestion == 0)
    {
        ILSection *sectionObj = [sections objectAtIndex:(data.currentSection - 1)];
        NSInteger numberOfQuestionsInPreviousSection = [[data getQuestionsBySection:(sectionObj.sectionID)] count];
        
        data.currentSection--;
        data.currentQuestion = numberOfQuestionsInPreviousSection - 1;
        
        _questionProgressBar.currentSection--;
        _questionProgressBar.currentSubSection = numberOfQuestionsInPreviousSection - 1;
    }
    else
    {
        data.currentQuestion--;
        _questionProgressBar.currentSubSection--;
    }
    
    [self.tableView reloadData];
}

- (void)changeSlider: (UISlider *)sender
{
    ILQuestion *q = [self getCurrentQuestion:[self getCurrentSection]];
    ILChoice *c = [[ILChoice alloc] initWithText:@"From UISlider" andValue:[NSString stringWithFormat:@"%d", (int)sender.value]];
    [data.responses setObject:c forKey:[NSString stringWithFormat:@"%d", q.questionID]];
    
    [self.tableView reloadData];
}

- (ILSection *)getCurrentSection
{
    ILSection *section = [sections objectAtIndex:data.currentSection];
    return section;
}

- (ILQuestion *)getCurrentQuestion:(ILSection *)section
{
    ILQuestion *question = [section.questions objectAtIndex:data.currentQuestion];
    return question;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 1)
    {
        ILQuestion *q = [self getCurrentQuestion:[self getCurrentSection]];
        if (q.type == RADIO)
        {
            NSArray *choices = [self getCurrentSection].choices;
            return [choices count];
        }
        else return 2;

    }
    else
    {
        return 1;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ILSection *section = [self getCurrentSection];
    ILQuestion *question = [self getCurrentQuestion:section];
    
    if (indexPath.section == 0)
    {
        NSString *questionHeader;
        
        if (section.sectionDescription.length == 0)
        {
            questionHeader = question.questionDescription;
        }
        else
        {
            questionHeader = [NSString stringWithFormat:@"%@\n\n%@", section.sectionDescription, question.questionDescription];
        }
        
        return MAX(([questionHeader sizeWithFont:[UIFont italicSystemFontOfSize:15] constrainedToSize:CGSizeMake(280, 9000) lineBreakMode:NSLineBreakByWordWrapping].height) + 14, 44);
    }
    if (indexPath.section == 1)
    {
        if (question.type == NUMSCALE)
        {
            if (indexPath.row == 0) return 44;
            if (indexPath.row == 1) return 22;
        }
        if (question.type == YESNO)
        {
            return 44;
        }
        ILChoice *choice = [section.choices objectAtIndex:indexPath.row];
        return MAX(([choice.text  sizeWithFont:[UIFont systemFontOfSize:15] constrainedToSize:CGSizeMake(280, 9000) lineBreakMode:NSLineBreakByWordWrapping].height) + 14, 44);
    }
    else return 44;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *TextCellIdentifier = @"TextCell";
    static NSString *ButtonCellIdentifier = @"ButtonCell";
    
    ILSection *s = [self getCurrentSection];
    ILQuestion *q = [self getCurrentQuestion:[self getCurrentSection]];
    
    UITableViewCell *cell;
    
    if (q.type == NUMSCALE)
    {
        cell = [tableView dequeueReusableCellWithIdentifier:ButtonCellIdentifier];
    }
    else
    {
        cell = [tableView dequeueReusableCellWithIdentifier:TextCellIdentifier];
        
    }
    
    if (cell == nil)
    {
        if (q.type == NUMSCALE)
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ButtonCellIdentifier];
        }
        else
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:TextCellIdentifier];
        }
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    if (indexPath.section == 0)
    {
        cell.textLabel.font = [UIFont italicSystemFontOfSize:15];
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        if (s.sectionDescription.length == 0)
        {
            cell.textLabel.text = q.questionDescription;
        }
        else
        {
            cell.textLabel.text = [NSString stringWithFormat:@"%@\n\n%@", s.sectionDescription, q.questionDescription];
        }
        
    }
    else if (indexPath.section == 1)
    {
        if (q.type == NUMSCALE)
        {
            if (indexPath.row == 0)
            {
                if ([cell viewWithTag:64])
                {
                    UISlider *_slider = (UISlider *)[cell viewWithTag:64];
                    ILChoice *response = [data.responses objectForKey:[NSString stringWithFormat:@"%d", q.questionID]];
                    
                    if (response)
                    {
                        _slider.value = [response.value integerValue];
                        
                    }
                    else
                    {
                        _slider.value = _slider.minimumValue;
                    }
                    
                }
                else
                {
                    CGFloat w = cell.frame.size.width;
                    CGFloat h = cell.frame.size.height;
                    
                    UISlider *_slider = [[UISlider alloc] initWithFrame:CGRectMake(0+15, 0, w-30, h)];
                    
                    ILChoice *minChoice = [[self getCurrentSection].choices objectAtIndex:0];
                    ILChoice *maxChoice = [[self getCurrentSection].choices objectAtIndex:1];
                    
                    NSInteger minValue = [minChoice.value integerValue];
                    NSInteger maxValue = [maxChoice.value integerValue];
                    
                    _slider.minimumValue = minValue;
                    _slider.maximumValue = maxValue;
                    _slider.continuous = NO;
                    
                    [_slider addTarget:self action:@selector(changeSlider:) forControlEvents:UIControlEventValueChanged];
                    _slider.tag = 64;
                    [cell addSubview:_slider];
                }
            }
            else if (indexPath.row == 1)
            {
                cell.textLabel.font = [UIFont systemFontOfSize:15];

                ILChoice *response = [data.responses objectForKey:[NSString stringWithFormat:@"%d", q.questionID]];
                if ([response.value isEqualToString:@"N/A"])
                {
                    cell.textLabel.text = @"1";
                }
                else
                {
                    cell.textLabel.text = response.value;
                }
                cell.textLabel.textAlignment = NSTextAlignmentCenter;
            }
        }
        else if (q.type == YESNO)
        {
            cell.textLabel.font = [UIFont systemFontOfSize:15];
            cell.textLabel.numberOfLines = 0;
            cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
            
            if (indexPath.row == 0)
            {
                cell.textLabel.text = @"Yes";
            }
            else if (indexPath.row == 1)
            {
                cell.textLabel.text = @"No";
            }
            
            ILChoice *response = [data.responses objectForKey:[NSString stringWithFormat:@"%d", q.questionID]];
            
            if (response)
            {
                if ([response.value isEqualToString:@"1"] && indexPath.row == 0)
                {
                    cell.accessoryType = UITableViewCellAccessoryCheckmark;
                }                
                else if ([response.value isEqualToString:@"0"] && indexPath.row == 1)
                {
                    cell.accessoryType = UITableViewCellAccessoryCheckmark;
                }
                else
                {
                    cell.accessoryType = UITableViewCellAccessoryNone;
                }
                
            }
            else
            {
                cell.accessoryType = UITableViewCellAccessoryNone;
            }

        }
        else
        {
            ILChoice *choice = [[self getCurrentSection].choices objectAtIndex:indexPath.row];
            cell.textLabel.font = [UIFont systemFontOfSize:15];
            cell.textLabel.numberOfLines = 0;
            cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
            cell.textLabel.text = choice.text;
            
            ILChoice *response = [data.responses objectForKey:[NSString stringWithFormat:@"%d", q.questionID]];
            
            if (response)
            {
                if (response.value == choice.value)
                {
                    cell.accessoryType = UITableViewCellAccessoryCheckmark;
                }
                else
                {
                    cell.accessoryType = UITableViewCellAccessoryNone;
                }
                
            }
            else
            {
                cell.accessoryType = UITableViewCellAccessoryNone;
            }

        }
    }
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 1)
    {
        ILQuestion *q = [self getCurrentQuestion:[self getCurrentSection]];
        
        if (q.type == NUMSCALE)
        {
            
        }
        else if (q.type == YESNO)
        {
            ILChoice *choice;
            
            if (indexPath.row == 0)
            {
                choice = [[ILChoice alloc] initWithText:@"YES" andValue:@"1"];
            }
            else
            {
                choice = [[ILChoice alloc] initWithText:@"NO" andValue:@"0"];
            }
            [data.responses setObject:choice forKey:[NSString stringWithFormat:@"%d", q.questionID]];
        }
        else
        {
            ILChoice *choice = [[self getCurrentSection].choices objectAtIndex:indexPath.row];
            [data.responses setObject:choice forKey:[NSString stringWithFormat:@"%d", q.questionID]];
        }
    }
    [self.tableView reloadData];
}

@end