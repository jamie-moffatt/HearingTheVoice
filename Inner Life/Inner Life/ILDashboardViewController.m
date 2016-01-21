//
//  ILDashboardViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILDashboardViewController.h"

#import "ILUserFormViewController.h"
#import "ILQuestionFormViewController.h"
#import "ILSettingsViewController.h"
#import "ILEndViewController.h"

#import "ILAppManager.h"
#import "ILTimeUtils.h"
#import "common.h"
#import "TestFlight.h"

@interface ILDashboardViewController ()

@end

@implementation ILDashboardViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
       
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [TestFlight passCheckpoint:@"Opened Dashboard"];
    self.title = @"Dashboard";
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Settings" style:UIBarButtonItemStyleBordered target:self action:@selector(settingsButton:)];
    
    [ILAppManager setDefaults];
    
    if ([ILAppManager isFirstRun])
    {
        [TestFlight passCheckpoint:@"Set Start Date"];
        [ILAppManager setFirstRun:NO];
        [ILAppManager setStartDate:[NSDate date]];
        [ILAppManager setupNotifications];
    }
    
    NSLog(@"%@", [ILAppManager userDefaultsToString]);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if (_needsJump)
    {
        _needsJump = NO;
        ILQuestionFormViewController *questionFormView = [[ILQuestionFormViewController alloc] initWithNibName:nil bundle:nil];
        questionFormView.currentSession = _sessionToJumpTo;
        [self.navigationController pushViewController:questionFormView animated:YES];
    }
    
    _clock.amStart = [ILAppManager getAMNotificationTime];
    _clock.amEnd = [ILAppManager getAMNotificationTime] + 3;
    _clock.pmStart = [ILAppManager getPMNotificationTime];
    _clock.pmEnd = [ILAppManager getPMNotificationTime] + 3;
    
    _clock.time = [ILTimeUtils secondsOnCurrentDay];
}

- (void)updateSessionProgressBar
{
    NSDate *startDate = [ILAppManager getStartDate];
    NSInteger recommendedSession = [ILTimeUtils getSessionByRegistrationDate:startDate];
    
    NSMutableArray *xs = [[NSMutableArray alloc] initWithCapacity:28];
    for (int i = 1; i <= 28; i++)
    {
        NSNumber* sessionIsComplete = [[ILAppManager getSessionsCompleted] objectForKey:[NSString stringWithFormat:@"%d", i]];
        NSNumber* sessionIsSubmitted = [[ILAppManager getSessionsSubmitted] objectForKey:[NSString stringWithFormat:@"%d", i]];
        
        if (i > recommendedSession)
        {
            [xs addObject:[NSNumber numberWithInt:EMPTY]];
        }
        else
        {
            if ([sessionIsComplete boolValue] && [sessionIsSubmitted boolValue])
            {
                [xs addObject:[NSNumber numberWithInt:BLUE]];
            }
            else if ([sessionIsComplete boolValue])
            {
                [xs addObject:[NSNumber numberWithInt:PURPLE]];
            }
            else
            {
                [xs addObject:[NSNumber numberWithInt:GREY]];
            }
        }
    }
    [_sessionProgressBar setSegmentMap:xs];
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    if (![ILAppManager userIsRegistered])
    {
        ILUserFormViewController *userFormView = [[ILUserFormViewController alloc] initWithNibName:@"ILUserForm" bundle:nil];
        userFormView.dashboard = self;
        [self presentViewController:userFormView animated:YES completion:nil];
    }
    
    [self updateSessionProgressBar];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)answerQuestions:(id)sender
{
    NSInteger session = [ILTimeUtils getSessionByRegistrationDate:[ILAppManager getStartDate]];
    if (session == -1) return;
    ILQuestionFormViewController *questionFormView = [[ILQuestionFormViewController alloc] initWithNibName:nil bundle:nil];
    questionFormView.currentSession = session;
    questionFormView.dashboard = self;
    [self.navigationController pushViewController:questionFormView animated:YES];
}

- (IBAction)sync:(UIButton *)sender
{
    for (int i = 1; i <= 28; i++)
    {
        NSNumber* sessionIsComplete = [[ILAppManager getSessionsCompleted] objectForKey:[NSString stringWithFormat:@"%d", i]];
        NSNumber* sessionIsSubmitted = [[ILAppManager getSessionsSubmitted] objectForKey:[NSString stringWithFormat:@"%d", i]];
        
        if ([sessionIsComplete boolValue] && ![sessionIsSubmitted boolValue])
        {
            NSFileManager* fm = [[NSFileManager alloc] init];
            NSURL* docsurl = [fm URLForDirectory:NSDocumentDirectory
                                        inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:nil];
            NSURL* submissionFolder = [docsurl URLByAppendingPathComponent:@"ILSubmissions"];
            
            NSError* error = nil;
            NSString* responseXML = [NSString stringWithContentsOfURL:[submissionFolder URLByAppendingPathComponent:[NSString stringWithFormat:@"submission%d.xml", i]] encoding:NSUTF8StringEncoding error:&error];
            
            if (!error)
            {
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
                         
                         NSInteger http_status_code = [((NSHTTPURLResponse *)response) statusCode];
                         
                         // HTTP (201 Created).
                         if (http_status_code == 201)
                         {
                             NSMutableDictionary *submitted = [NSMutableDictionary dictionaryWithDictionary:[ILAppManager getSessionsSubmitted]];
                             [submitted setObject:[NSNumber numberWithBool:YES] forKey:[NSString stringWithFormat:@"%d", i]];
                             [ILAppManager setSessionsSubmitted:submitted];
                         }
                     }
                     else
                     {
                         UIAlertView *errorAlert = [[UIAlertView alloc] initWithTitle:@"Failed Connection." message:@"Could not synchronize." delegate:self cancelButtonTitle:@"Dismiss" otherButtonTitles:nil];
                         [errorAlert show];
                     }
                 }];
                
            }
            else
            {
                UIAlertView *errorAlert = [[UIAlertView alloc] initWithTitle:@"Failed Connection." message:@"Could not synchronize." delegate:self cancelButtonTitle:@"Dismiss" otherButtonTitles:nil];
                [errorAlert show];
            }
        }
    }
    
    [self updateSessionProgressBar];
}

- (void)settingsButton:(UIButton *)sender
{
    ILSettingsViewController *settingsView = [[ILSettingsViewController alloc] initWithNibName:nil bundle:nil];
    [self.navigationController pushViewController:settingsView animated:YES];
}

@end
